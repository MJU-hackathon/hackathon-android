package com.cookandroid.mjhackathon.data.repository.postDaily

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.postDaily.response.CreateDiaryResponse
import com.cookandroid.mjhackathon.data.model.postDaily.CreateDiaryRequest
import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult
import com.cookandroid.mjhackathon.presentation.postDaily.PostDailyUiState
import kotlinx.serialization.json.Json
import okhttp3.RequestBody
import okio.Buffer
import org.junit.Assert.*
import org.junit.Test

class DiaryMultipartTest {
    private val draftId = "f904e2ab-6933-4a4e-8f7d-cb177b51d55c"
    private val request = CreateDiaryRequest(
        draftId = draftId, draftStartedAt = "2026-11-06T14:59:00Z", questId = "922337203685477000",
        questTitle = "시장 탐험", location = "영천시장", result = DiaryResult.DID,
        photoUri = "content://example/photo", memo = "",
    )
    private fun RequestBody.text(): String = Buffer().also { writeTo(it) }.readUtf8()

    @Test fun blankMemoIsIncludedAndDoesNotPreventSaving() {
        val fields = request.toMultipartFields()
        assertEquals("", fields.memo.text())
        assertEquals(request.questId, fields.questId.text())
        assertEquals("EXPERIENCED", fields.outcome.text())
        assertTrue(PostDailyUiState(isLoading = false, hasPhoto = true, result = DiaryResult.DID).canSave)
        assertTrue(PostDailyUiState(isLoading = false, hasPhoto = true, result = DiaryResult.MISSED).canSave)
        assertFalse(PostDailyUiState(isLoading = false, hasPhoto = true).canSave)
    }

    @Test fun retryKeepsCaptureIdAndStartTimeEvenWhenMemoChanges() {
        val first = request.toMultipartFields()
        val retry = request.copy(memo = "메모 추가", result = DiaryResult.MISSED).toMultipartFields()
        assertEquals(draftId, first.draftId.text())
        assertEquals(first.draftId.text(), retry.draftId.text())
        assertEquals(first.draftStartedAt.text(), retry.draftStartedAt.text())
        assertEquals("NOT_EXPERIENCED", retry.outcome.text())
    }

    @Test fun acceptsFiftyCodePointsAndRejectsFiftyOne() {
        request.copy(memo = "😀".repeat(50)).toMultipartFields()
        try { request.copy(memo = "가".repeat(51)).toMultipartFields(); fail("Expected memo limit") }
        catch (_: IllegalArgumentException) { }
    }

    @Test fun rejectsInvalidMetadataBeforeUpload() {
        for (invalid in listOf(request.copy(questId = "mock-food"), request.copy(draftId = "bad-id"))) {
            try { invalid.toMultipartFields(); fail("Expected invalid input") }
            catch (_: IllegalArgumentException) { }
        }
        try { request.copy(draftStartedAt = "2026-11-06").toMultipartFields(); fail("Expected ISO timestamp") }
        catch (_: java.time.format.DateTimeParseException) { }
    }

    @Test fun mapsServerIdsPhotoAndBothOutcomes() {
        val response = Json.decodeFromString<BaseResponse<CreateDiaryResponse>>(
            """{"status":200,"code":"SUCCESS","message":"ok","data":{"diaryId":1201,"questId":922337203685477000,"title":"시장 탐험","placeName":"영천시장","photoUrl":"https://example.com/photo.jpg","outcome":"NOT_EXPERIENCED","memo":null,"recordedAt":"2026-11-06T15:01:00Z"}}"""
        ).data
        val saved = response.toSavedDiary(request.questId)
        assertEquals("1201", saved.id)
        assertEquals("https://example.com/photo.jpg", saved.photoUri)
        assertEquals(DiaryResult.MISSED, saved.result)
        assertEquals("", saved.memo)
        assertEquals(DiaryResult.DID, response.copy(outcome = "EXPERIENCED").toSavedDiary(request.questId).result)
        try { response.copy(questId = 7).toSavedDiary(request.questId); fail("Expected mismatched quest") }
        catch (_: IllegalStateException) { }
    }

    @Test fun savingAndTerminalConflictPreventRepeatedSubmission() {
        val state = PostDailyUiState(isLoading = false, hasPhoto = true, result = DiaryResult.DID)
        assertFalse(state.copy(isSaving = true).canSave)
        assertFalse(state.copy(savedDiaryId = "1201").canSave)
        assertFalse(state.copy(saveBlocked = true).canSave)
        assertTrue(state.copy(saveError = "네트워크 오류").canSave)
    }
}
