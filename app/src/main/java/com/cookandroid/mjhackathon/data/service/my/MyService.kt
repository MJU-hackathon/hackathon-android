package com.cookandroid.mjhackathon.data.service.my

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.my.response.DiaryListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MyService {
    @GET("api/diaries")
    suspend fun getDiaries(@Query("locale") locale: String? = null): BaseResponse<DiaryListResponse>
}
