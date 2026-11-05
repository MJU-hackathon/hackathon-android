package com.cookandroid.mjhackathon.data.repository.home

import com.cookandroid.mjhackathon.data.dto.home.request.EnsureDailyQuestsRequest
import com.cookandroid.mjhackathon.data.dto.home.response.DailyQuestResponse

interface HomeRepository {
    suspend fun getTodayQuests(locale: String? = null): List<DailyQuestResponse>
    suspend fun ensureDailyQuests(request: EnsureDailyQuestsRequest): List<DailyQuestResponse>
    suspend fun getOrCreateTodayQuests(locale: String): List<DailyQuestResponse>
}
