package com.cookandroid.mjhackathon.data.dto.home.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnsureDailyQuestsRequest(
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("locale") val locale: String,
)
