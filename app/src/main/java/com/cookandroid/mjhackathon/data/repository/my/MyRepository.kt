package com.cookandroid.mjhackathon.data.repository.my

import com.cookandroid.mjhackathon.data.dto.my.response.ExampleResponse

interface MyRepository {
    suspend fun getMyData(): List<ExampleResponse>
}
