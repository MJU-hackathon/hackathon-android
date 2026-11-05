package com.cookandroid.mjhackathon.data.dto.home.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExampleResponse(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)
