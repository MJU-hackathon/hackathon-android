package com.cookandroid.mjhackathon.data.repository.home

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.home.request.EnsureDailyQuestsRequest
import com.cookandroid.mjhackathon.data.dto.home.response.DailyQuestResponse
import com.cookandroid.mjhackathon.data.network.ApiException
import com.cookandroid.mjhackathon.data.repository.location.CurrentLocationProvider
import com.cookandroid.mjhackathon.data.repository.location.QuestCoordinates
import com.cookandroid.mjhackathon.data.service.home.HomeService
import java.io.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class HomeRepositoryTest {
    private val quest = DailyQuestResponse(922337203685477000L, "FOOD", 30, "시장 탐험", "영천시장", null, "추천 이유")
    private val service = FakeHomeService()
    private var locationCalls = 0
    private val locationProvider = object : CurrentLocationProvider {
        override suspend fun getCurrentLocation(): QuestCoordinates {
            locationCalls++
            return QuestCoordinates(37.5718, 126.962)
        }
    }
    private val repository = HomeRepositoryImpl(service, locationProvider)

    @Test fun existingSetUsesTodayWithoutLocationOrEnsure() = runBlocking {
        service.today = listOf(quest)
        assertEquals(listOf(quest), repository.getOrCreateTodayQuests("vi"))
        assertEquals("vi", service.locale)
        assertEquals(0, locationCalls)
        assertNull(service.request)
    }

    @Test fun emptySetStaysEmptyWithoutGeneratingMoreQuests() = runBlocking {
        assertTrue(repository.getOrCreateTodayQuests("ko").isEmpty())
        assertEquals(0, locationCalls)
        assertNull(service.request)
    }

    @Test fun missingSetUsesRealCoordinatesAndLocale() = runBlocking {
        service.failure = httpError(404, "DAILY_SET_NOT_FOUND")
        service.ensured = listOf(quest)
        assertEquals(listOf(quest), repository.getOrCreateTodayQuests("ja"))
        assertEquals(1, locationCalls)
        assertEquals(EnsureDailyQuestsRequest(37.5718, 126.962, "ja"), service.request)
    }

    @Test fun otherErrorsNeverGenerateASet() = runBlocking {
        for ((status, code) in listOf(404 to "PROFILE_NOT_FOUND", 500 to "INTERNAL_SERVER_ERROR", 401 to "USER_KEY_REQUIRED", 500 to "DAILY_SET_NOT_FOUND")) {
            service.failure = httpError(status, code)
            try {
                repository.getOrCreateTodayQuests("ko")
                fail("Expected API error")
            } catch (error: ApiException) {
                assertEquals(code, error.code)
            }
        }
        service.failure = IOException("Server offline")
        try { repository.getOrCreateTodayQuests("ko"); fail("Expected network error") }
        catch (_: IOException) { }
        assertEquals(0, locationCalls)
        assertNull(service.request)
    }

    @Test fun invalidCoordinatesAreNotSent() = runBlocking {
        for (latitude in listOf(91.0, Double.NaN)) {
            try { repository.ensureDailyQuests(EnsureDailyQuestsRequest(latitude, 126.0, "ko")); fail("Expected validation error") }
            catch (_: IllegalArgumentException) { }
        }
        assertNull(service.request)
    }

    @Test fun parsesQuestResponseWithLongIdAndNullableImage() {
        val response = Json.decodeFromString<BaseResponse<List<DailyQuestResponse>>>(
            """{"status":200,"code":"SUCCESS","message":"ok","data":[{"questId":922337203685477000,"category":"FOOD","estimatedMinutes":30,"title":"시장 탐험","placeName":"영천시장","imageUrl":null,"reason":"추천 이유"}]}"""
        )
        assertEquals(listOf(quest), response.data)
    }

    private fun httpError(status: Int, code: String): HttpException = HttpException(
        Response.error<Any>(status, """{"code":"$code","message":"error","data":null}""".toResponseBody("application/json".toMediaType()))
    )

    private class FakeHomeService : HomeService {
        var today = emptyList<DailyQuestResponse>()
        var ensured = emptyList<DailyQuestResponse>()
        var failure: Exception? = null
        var request: EnsureDailyQuestsRequest? = null
        var locale: String? = null
        override suspend fun getTodayQuests(locale: String?): BaseResponse<List<DailyQuestResponse>> {
            this.locale = locale
            failure?.let { throw it }
            return BaseResponse("SUCCESS", "ok", today)
        }
        override suspend fun ensureDailyQuests(request: EnsureDailyQuestsRequest): BaseResponse<List<DailyQuestResponse>> {
            this.request = request
            return BaseResponse("SUCCESS", "ok", ensured)
        }
    }
}
