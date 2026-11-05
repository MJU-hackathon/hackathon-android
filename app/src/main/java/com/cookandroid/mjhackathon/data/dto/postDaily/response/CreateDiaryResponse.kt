package com.cookandroid.mjhackathon.data.dto.postDaily.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateDiaryResponse(
    @SerialName("diaryId") val diaryId: Long,
    @SerialName("questId") val questId: Long,
    @SerialName("title") val title: String,
    @SerialName("placeName") val placeName: String,
    @SerialName("photoUrl") val photoUrl: String,
    @SerialName("outcome") val outcome: String,
    @SerialName("memo") val memo: String? = null,
    @SerialName("recordedAt") val recordedAt: String,
)
