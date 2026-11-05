package com.cookandroid.mjhackathon.data.repository.my

import com.cookandroid.mjhackathon.data.dto.my.response.ExampleResponse
import com.cookandroid.mjhackathon.data.service.my.MyService
import jakarta.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val myService: MyService,
) : MyRepository {
    override suspend fun getMyData(): List<ExampleResponse> =
        myService.getExampleData().data
}
