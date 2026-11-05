package com.cookandroid.mjhackathon.data.repository.detail

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.detail.response.PassQuestResponse
import com.cookandroid.mjhackathon.data.dto.detail.response.QuestDetailResponse
import com.cookandroid.mjhackathon.data.network.ApiException
import com.cookandroid.mjhackathon.data.service.detail.DetailService
import com.cookandroid.mjhackathon.presentation.detail.DetailUiState
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class DetailRepositoryTest {
    private val id = 922337203685477000L
    private val detail = QuestDetailResponse(
        questId = id, dailySetId = 7L, placeKey = "market", activityKey = "food",
        category = "FOOD", estimatedMinutes = 30, title = "시장 탐험", reason = "추천 이유",
        description = "활동 설명", steps = listOf("방문하기", "주문하기"), placeName = "영천시장",
        locale = "vi", status = "AVAILABLE",
    )
    private val service = FakeService()
    private val repository = DetailRepositoryImpl(service)

    @Test fun mapsLongIdLocaleAndMissingCoordinates() = runBlocking {
        val quest = repository.getQuestDetail(id.toString(), "vi")
        assertEquals(id, service.requestId)
        assertEquals("vi", service.locale)
        assertEquals(id.toString(), quest.id)
        assertEquals(detail.steps, quest.steps)
        assertEquals(detail.reason, quest.reason)
        assertEquals(30, quest.estimatedMinutes)
        assertNull(quest.address)
        assertFalse(quest.hasMapCoordinates)
        assertTrue(DetailUiState(isLoading = false, quest = quest).canAct)
        assertFalse(DetailUiState(isLoading = false, quest = quest.copy(status = "SKIPPED")).canAct)
        assertFalse(quest.copy(latitude = 91.0, longitude = 127.0).hasMapCoordinates)
        assertFalse(quest.copy(latitude = 37.0, longitude = null).hasMapCoordinates)
        assertTrue(quest.copy(latitude = 37.0, longitude = 127.0).hasMapCoordinates)
    }

    @Test fun decodesNullableServerFields() {
        val parsed = Json.decodeFromString<BaseResponse<QuestDetailResponse>>(
            """{"status":200,"code":"SUCCESS","message":"ok","data":{"questId":922337203685477000,"dailySetId":7,"placeKey":"market","activityKey":"food","category":"FOOD","estimatedMinutes":30,"title":"시장 탐험","reason":"추천 이유","description":"활동 설명","steps":["방문하기","주문하기"],"placeName":"영천시장","address":null,"latitude":null,"longitude":null,"imageUrl":null,"locale":"vi","status":"AVAILABLE"}}"""
        )
        assertEquals(detail, parsed.data)
    }

    @Test fun rejectsInvalidIdsBeforeRequest() = runBlocking {
        for (invalid in listOf("mock-food", "", "0", "-1", "9223372036854775808")) {
            try { repository.getQuestDetail(invalid); fail("Expected invalid ID") }
            catch (_: IllegalArgumentException) { }
            try { repository.passQuest(invalid); fail("Expected invalid ID") }
            catch (_: IllegalArgumentException) { }
        }
        assertNull(service.requestId)
    }

    @Test fun onlySkippedResultForSameQuestIsSuccessful() = runBlocking {
        repository.passQuest(id.toString())
        assertEquals(id, service.requestId)
        for (wrong in listOf(PassQuestResponse(id, "AVAILABLE", 3), PassQuestResponse(id + 1, "SKIPPED", 2))) {
            service.passResult = wrong
            try { repository.passQuest(id.toString()); fail("Expected invalid pass result") }
            catch (_: IllegalStateException) { }
        }
    }

    @Test fun propagatesNotFoundAndConflictCodes() = runBlocking {
        for ((status, code) in listOf(404 to "QUEST_NOT_FOUND", 409 to "QUEST_NOT_AVAILABLE", 401 to "USER_KEY_REQUIRED", 500 to "INTERNAL_SERVER_ERROR")) {
            service.failure = HttpException(Response.error<Any>(status,
                """{"code":"$code","message":"error","data":null}""".toResponseBody("application/json".toMediaType())))
            try { repository.passQuest(id.toString()); fail("Expected API error") }
            catch (error: ApiException) { assertEquals(code, error.code); assertEquals(status, error.httpStatus) }
        }
    }

    @Test fun rejectsDifferentQuestInDetailResponse() = runBlocking {
        service.detailResult = detail.copy(questId = id + 1)
        try { repository.getQuestDetail(id.toString()); fail("Expected mismatched quest ID") }
        catch (_: IllegalStateException) { }
    }

    private inner class FakeService : DetailService {
        var requestId: Long? = null
        var locale: String? = null
        var detailResult = detail
        var passResult = PassQuestResponse(id, "SKIPPED", 2)
        var failure: HttpException? = null
        override suspend fun getQuestDetail(questId: Long, locale: String?): BaseResponse<QuestDetailResponse> {
            requestId = questId
            this.locale = locale
            failure?.let { throw it }
            return BaseResponse("SUCCESS", "ok", detailResult)
        }
        override suspend fun passQuest(questId: Long): BaseResponse<PassQuestResponse> {
            requestId = questId
            failure?.let { throw it }
            return BaseResponse("SUCCESS", "ok", passResult)
        }
    }
}
