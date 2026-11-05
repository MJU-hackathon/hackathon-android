package com.cookandroid.mjhackathon.data.repository.my

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.my.response.DiaryEntryResponse
import com.cookandroid.mjhackathon.data.dto.my.response.DiaryGroupResponse
import com.cookandroid.mjhackathon.data.dto.my.response.DiaryListResponse
import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult
import com.cookandroid.mjhackathon.data.network.ApiException
import com.cookandroid.mjhackathon.data.service.my.MyService
import com.cookandroid.mjhackathon.presentation.my.MyUiState
import java.io.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class MyRepositoryTest {
    private val memo = "  오늘의 메모\n번역하지 않는 원문 😀  "
    private val entry = DiaryEntryResponse(922337203685477000L, 1201L, "시장 탐험", "영천시장",
        "https://example.com/photo.jpg", "NOT_EXPERIENCED", memo, "2026-10-31T16:30:00Z")
    private val service = FakeService()
    private val repository = MyRepositoryImpl(service)

    @Test fun retainsServerCountsGroupsAndOriginalMemo() = runBlocking {
        service.data = DiaryListResponse(17, 12, 5, listOf(DiaryGroupResponse("2026-11", listOf(entry))))
        val collection = repository.getDiaries("vi")
        assertEquals("vi", service.locale)
        val state = MyUiState.fromCollection(collection)
        assertEquals(17, state.totalCount)
        assertEquals(12, state.didCount)
        assertEquals(5, state.missedCount)
        val month = state.months.single()
        assertEquals("2026-11", month.key)
        assertEquals("2026년 11월", month.title)
        val record = month.entries.single()
        assertEquals("2026.11.01", record.displayDate)
        assertEquals(memo, record.diary.memo)
        assertEquals("922337203685477000", record.diary.id)
        assertEquals(DiaryResult.MISSED, record.diary.result)
        assertEquals(entry.photoUrl, record.diary.photoUri)
    }

    @Test fun emptyResponseIsSuccessfulEmptyState() = runBlocking {
        val state = MyUiState.fromCollection(repository.getDiaries())
        assertNull(service.locale)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(0, state.totalCount)
        assertTrue(state.months.isEmpty())
    }

    @Test fun retainsServerOrderAndMapsBothOutcomes() = runBlocking {
        service.data = DiaryListResponse(2, 1, 1, listOf(
            DiaryGroupResponse("2026-11", listOf(entry.copy(outcome = "EXPERIENCED", memo = ""))),
            DiaryGroupResponse("2026-10", listOf(entry.copy(diaryId = 1202, recordedAt = "2026-10-01T01:00:00Z"))),
        ))
        val state = MyUiState.fromCollection(repository.getDiaries())
        assertEquals(listOf("2026-11", "2026-10"), state.months.map { it.key })
        assertEquals(DiaryResult.DID, state.months.first().entries.single().diary.result)
        assertEquals("", state.months.first().entries.single().diary.memo)
    }

    @Test fun errorsPropagateWithoutLocalFallback() = runBlocking {
        val offline = IOException("offline")
        service.failure = offline
        try { repository.getDiaries(); fail("Expected network error") }
        catch (error: IOException) { assertSame(offline, error) }
        service.failure = HttpException(Response.error<Any>(404,
            """{"code":"PROFILE_NOT_FOUND","message":"error","data":null}""".toResponseBody("application/json".toMediaType())))
        try { repository.getDiaries(); fail("Expected profile error") }
        catch (error: ApiException) { assertEquals("PROFILE_NOT_FOUND", error.code) }
    }

    @Test fun decodesEmptyServerResponse() {
        val result = Json.decodeFromString<BaseResponse<DiaryListResponse>>(
            """{"status":200,"code":"SUCCESS","message":"ok","data":{"totalCount":0,"experiencedCount":0,"notExperiencedCount":0,"groups":[]}}"""
        )
        assertTrue(result.data.groups.isEmpty())
        assertEquals(0, result.data.totalCount)
    }

    private class FakeService : MyService {
        var data = DiaryListResponse(0, 0, 0, emptyList())
        var locale: String? = null
        var failure: Exception? = null
        override suspend fun getDiaries(locale: String?): BaseResponse<DiaryListResponse> {
            this.locale = locale
            failure?.let { throw it }
            return BaseResponse("SUCCESS", "ok", data)
        }
    }
}
