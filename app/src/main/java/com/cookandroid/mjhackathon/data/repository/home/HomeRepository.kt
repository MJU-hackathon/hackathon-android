package com.cookandroid.mjhackathon.data.repository.home

import com.cookandroid.mjhackathon.data.dto.home.response.ExampleResponse

interface HomeRepository {
    suspend fun getHomes(): List<ExampleResponse>
}
