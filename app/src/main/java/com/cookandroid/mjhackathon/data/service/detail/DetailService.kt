package com.cookandroid.mjhackathon.data.service.detail

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.detail.request.ExampleRequest
import com.cookandroid.mjhackathon.data.dto.detail.response.ExampleResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//예시 Service 형태.
interface DetailService {
    //안에 suspend함수들 다 바꿔야됨
    @GET("api/v1/data")
    suspend fun getExampleData(): BaseResponse<ExampleResponse>

    @POST("api/v1/data")
    suspend fun postExampleData(
        @Body exampleRequestDto: ExampleRequest,
    ): BaseResponse<Unit>
}
