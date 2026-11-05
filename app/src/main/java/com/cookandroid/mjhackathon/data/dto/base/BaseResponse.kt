package com.cookandroid.mjhackathon.data.dto.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//서버 데이터 응답값 기본 Response
@Serializable
data class BaseResponse<T>(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: T,
    @SerialName("status")
    val status: Int? = null,
)
