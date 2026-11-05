package com.cookandroid.mjhackathon.data.dto.detail.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PassQuestResponse(
    @SerialName("questId") val questId: Long,
    @SerialName("status") val status: String,
    @SerialName("remainingCount") val remainingCount: Int,
)
