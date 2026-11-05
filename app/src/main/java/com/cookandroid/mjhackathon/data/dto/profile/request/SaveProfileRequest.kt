package com.cookandroid.mjhackathon.data.dto.profile.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveProfileRequest(
    @SerialName("nickname") val nickname: String,
    @SerialName("locale") val locale: String,
    @SerialName("birthDate") val birthDate: String,
    @SerialName("stayStartDate") val stayStartDate: String,
    @SerialName("stayEndDate") val stayEndDate: String,
    @SerialName("interests") val interests: List<String>,
)
