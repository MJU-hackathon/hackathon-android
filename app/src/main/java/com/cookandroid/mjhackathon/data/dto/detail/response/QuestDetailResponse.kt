package com.cookandroid.mjhackathon.data.dto.detail.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestDetailResponse(
    @SerialName("questId") val questId: Long,
    @SerialName("dailySetId") val dailySetId: Long,
    @SerialName("placeKey") val placeKey: String,
    @SerialName("activityKey") val activityKey: String,
    @SerialName("category") val category: String,
    @SerialName("estimatedMinutes") val estimatedMinutes: Int,
    @SerialName("title") val title: String,
    @SerialName("reason") val reason: String,
    @SerialName("description") val description: String,
    @SerialName("steps") val steps: List<String>,
    @SerialName("placeName") val placeName: String,
    @SerialName("address") val address: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("locale") val locale: String,
    @SerialName("status") val status: String,
)
