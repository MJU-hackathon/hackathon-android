package com.cookandroid.mjhackathon.data.repository.postDaily

import com.cookandroid.mjhackathon.data.dto.postDaily.response.ExampleResponse
import com.cookandroid.mjhackathon.data.service.postDaily.PostDailyService
import jakarta.inject.Inject

class PostDailyRepositoryImpl @Inject constructor(
    private val postDailyService: PostDailyService,
) : PostDailyRepository {
    override suspend fun getPostDailies(): List<ExampleResponse> =
        postDailyService.getExampleData().data
}
