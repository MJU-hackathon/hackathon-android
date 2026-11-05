package com.cookandroid.mjhackathon.data.repository.postDaily

import com.cookandroid.mjhackathon.data.dto.postDaily.response.CreateDiaryResponse
import com.cookandroid.mjhackathon.data.model.postDaily.CreateDiaryRequest
import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult
import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary
import java.time.OffsetDateTime
import java.util.UUID
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

internal data class DiaryMultipartFields(
    val questId: RequestBody, val outcome: RequestBody, val memo: RequestBody,
    val draftId: RequestBody, val draftStartedAt: RequestBody,
)

internal fun CreateDiaryRequest.toMultipartFields(): DiaryMultipartFields {
    val id = requireNotNull(questId.toLongOrNull()?.takeIf { it > 0 })
    require(UUID.fromString(draftId).toString().equals(draftId, ignoreCase = true))
    OffsetDateTime.parse(draftStartedAt)
    require(memo.codePointCount(0, memo.length) <= 50)
    return DiaryMultipartFields(
        id.toString().textPart(), result.apiValue.textPart(), memo.textPart(),
        draftId.textPart(), draftStartedAt.textPart(),
    )
}

private fun String.textPart(): RequestBody = toRequestBody("text/plain; charset=utf-8".toMediaType())

internal fun CreateDiaryResponse.toSavedDiary(expectedQuestId: String): SavedDiary {
    check(questId.toString() == expectedQuestId && diaryId > 0) { "저장된 일기 정보를 확인하지 못했어요." }
    return SavedDiary(
        id = diaryId.toString(), questId = questId.toString(), questTitle = title,
        location = placeName, result = DiaryResult.fromApi(outcome), photoUri = photoUrl,
        memo = memo.orEmpty(), savedAt = recordedAt,
    )
}
