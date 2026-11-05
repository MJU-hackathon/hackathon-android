package com.cookandroid.mjhackathon.data.dto.postDaily.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 실제 API 명세에 맞게 필드를 변경해야 하는 예시 DTO.
@Serializable
data class ExampleRequest(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)
