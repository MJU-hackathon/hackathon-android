package com.cookandroid.mjhackathon.data.repository.detail

import com.cookandroid.mjhackathon.data.dto.detail.response.ExampleResponse

interface DetailRepository {
    suspend fun getDetail(): ExampleResponse
}
