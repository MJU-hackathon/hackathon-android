package com.cookandroid.mjhackathon.data.dto.home.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExampleRequest(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)
