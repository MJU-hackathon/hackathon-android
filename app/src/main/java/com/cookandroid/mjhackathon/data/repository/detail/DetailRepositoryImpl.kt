
package com.cookandroid.mjhackathon.data.repository.detail

import com.cookandroid.mjhackathon.data.dto.detail.response.ExampleResponse
import com.cookandroid.mjhackathon.data.service.detail.DetailService
import jakarta.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val detailService: DetailService,
) : DetailRepository {
    override suspend fun getDetail(): ExampleResponse =
        detailService.getExampleData().data
}
