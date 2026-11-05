package com.cookandroid.mjhackathon.data.repository.home

import com.cookandroid.mjhackathon.data.dto.home.request.EnsureDailyQuestsRequest
import com.cookandroid.mjhackathon.data.dto.home.response.DailyQuestResponse
import com.cookandroid.mjhackathon.data.network.ApiException
import com.cookandroid.mjhackathon.data.network.toApiException
import com.cookandroid.mjhackathon.data.repository.location.CurrentLocationProvider
import com.cookandroid.mjhackathon.data.service.home.HomeService
import jakarta.inject.Inject
import retrofit2.HttpException

class HomeRepositoryImpl @Inject constructor(
    private val homeService: HomeService,
    private val locationProvider: CurrentLocationProvider,
) : HomeRepository {
    override suspend fun getTodayQuests(locale: String?): List<DailyQuestResponse> =
        try { homeService.getTodayQuests(locale).data }
        catch (error: HttpException) { throw error.toApiException() }

    override suspend fun ensureDailyQuests(request: EnsureDailyQuestsRequest): List<DailyQuestResponse> {
        require(request.latitude.isFinite() && request.latitude in -90.0..90.0)
        require(request.longitude.isFinite() && request.longitude in -180.0..180.0)
        require(request.locale in setOf("ko", "en", "vi", "zh", "ja"))
        return try { homeService.ensureDailyQuests(request).data }
        catch (error: HttpException) { throw error.toApiException() }
    }

    override suspend fun getOrCreateTodayQuests(locale: String): List<DailyQuestResponse> {
        try {
            return getTodayQuests(locale)
        } catch (error: ApiException) {
            // 빈 배열, 프로필 없음, 서버 장애에는 새 묶음 생성을 시도하지 않는다.
            if (error.httpStatus != 404 || error.code != "DAILY_SET_NOT_FOUND") throw error
        }
        val location = locationProvider.getCurrentLocation()
        return ensureDailyQuests(EnsureDailyQuestsRequest(location.latitude, location.longitude, locale))
    }
}
