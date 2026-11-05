package com.cookandroid.mjhackathon.data.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException

class ApiException(val code: String?, val httpStatus: Int, cause: HttpException) : Exception(cause)

internal fun HttpException.toApiException(): ApiException {
    val errorCode = runCatching {
        Json.parseToJsonElement(response()?.errorBody()?.string().orEmpty())
            .jsonObject["code"]?.jsonPrimitive?.content
    }.getOrNull()
    return ApiException(errorCode, code(), this)
}
