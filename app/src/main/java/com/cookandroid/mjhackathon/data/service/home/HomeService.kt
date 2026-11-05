package com.cookandroid.mjhackathon.data.service.home

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.home.request.ExampleRequest
import com.cookandroid.mjhackathon.data.dto.home.response.ExampleResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//예시 Service 형태.
interface HomeService {
    //안에 예시 suspend 함수 다 바꿔야됨
    @GET("api/v1/data")
    suspend fun getExampleData(): BaseResponse<List<ExampleResponse>>

    @POST("api/v1/data")
    suspend fun postExampleData(
        @Body exampleRequestDto: ExampleRequest,
    ): BaseResponse<Unit>
}
