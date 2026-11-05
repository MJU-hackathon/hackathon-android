package com.cookandroid.mjhackathon.data.dto.home.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyQuestResponse(
    @SerialName("questId") val questId: Long,
    @SerialName("category") val category: String,
    @SerialName("estimatedMinutes") val estimatedMinutes: Int,
    @SerialName("title") val title: String,
    @SerialName("placeName") val placeName: String,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("reason") val reason: String,
)
