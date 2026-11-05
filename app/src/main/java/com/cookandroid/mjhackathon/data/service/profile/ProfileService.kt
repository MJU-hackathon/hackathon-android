package com.cookandroid.mjhackathon.data.service.profile

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.profile.request.ExampleRequest
import com.cookandroid.mjhackathon.data.dto.profile.response.ExampleResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// 예시 Service: 경로, 함수와 응답 타입은 실제 API 명세에 맞게 변경해야 함.
interface ProfileService {
    @GET("api/v1/data")
    suspend fun getExampleData(): BaseResponse<List<ExampleResponse>>

    @POST("api/v1/data")
    suspend fun postExampleData(
        @Body exampleRequestDto: ExampleRequest,
    ): BaseResponse<Unit>
}
