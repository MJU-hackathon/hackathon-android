package com.cookandroid.mjhackathon.data.service.detail

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.detail.response.QuestDetailResponse
import com.cookandroid.mjhackathon.data.dto.detail.response.PassQuestResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DetailService {
    @GET("api/quests/{questId}")
    suspend fun getQuestDetail(
        @Path("questId") questId: Long,
        @Query("locale") locale: String? = null,
    ): BaseResponse<QuestDetailResponse>

    @POST("api/quests/{questId}/pass")
    suspend fun passQuest(@Path("questId") questId: Long): BaseResponse<PassQuestResponse>
}
