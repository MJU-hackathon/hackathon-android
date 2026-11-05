package com.cookandroid.mjhackathon.data.service.postDaily

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.postDaily.response.CreateDiaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PostDailyService {
    @Multipart
    @POST("api/diaries")
    suspend fun createDiary(
        @Part photo: MultipartBody.Part,
        @Part("questId") questId: RequestBody,
        @Part("outcome") outcome: RequestBody,
        @Part("memo") memo: RequestBody,
        @Part("draftId") draftId: RequestBody,
        @Part("draftStartedAt") draftStartedAt: RequestBody,
    ): BaseResponse<CreateDiaryResponse>
}
