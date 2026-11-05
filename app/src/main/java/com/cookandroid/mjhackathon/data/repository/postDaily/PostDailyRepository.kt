package com.cookandroid.mjhackathon.data.repository.postDaily

import com.cookandroid.mjhackathon.data.dto.postDaily.response.ExampleResponse

interface PostDailyRepository {
    suspend fun getPostDailies(): List<ExampleResponse>
}
