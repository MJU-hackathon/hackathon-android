package com.cookandroid.mjhackathon.data.service.home

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.home.request.EnsureDailyQuestsRequest
import com.cookandroid.mjhackathon.data.dto.home.response.DailyQuestResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface HomeService {
    @POST("api/daily-quests/ensure")
    suspend fun ensureDailyQuests(
        @Body request: EnsureDailyQuestsRequest,
    ): BaseResponse<List<DailyQuestResponse>>

    @GET("api/daily-quests/today")
    suspend fun getTodayQuests(
        @Query("locale") locale: String? = null,
    ): BaseResponse<List<DailyQuestResponse>>
}
