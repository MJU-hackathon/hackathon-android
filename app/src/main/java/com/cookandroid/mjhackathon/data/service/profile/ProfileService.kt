package com.cookandroid.mjhackathon.data.service.profile

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import com.cookandroid.mjhackathon.data.dto.profile.request.SaveProfileRequest
import com.cookandroid.mjhackathon.data.dto.profile.response.ProfileResponse

// 예시 Service: 경로, 함수와 응답 타입은 실제 API 명세에 맞게 변경해야 함.
interface ProfileService {
    @PUT("api/profile")
    suspend fun saveProfile(@Body request: SaveProfileRequest): BaseResponse<ProfileResponse>
}
