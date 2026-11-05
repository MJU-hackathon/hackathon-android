package com.cookandroid.mjhackathon.data.repository.home

import com.cookandroid.mjhackathon.data.dto.home.response.ExampleResponse
import com.cookandroid.mjhackathon.data.service.home.HomeService
import jakarta.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeService: HomeService,
) : HomeRepository {
    override suspend fun getHomes(): List<ExampleResponse> =
        homeService.getExampleData().data
}
