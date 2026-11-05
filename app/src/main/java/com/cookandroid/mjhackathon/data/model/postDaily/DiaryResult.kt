package com.cookandroid.mjhackathon.data.model.postDaily

import kotlinx.serialization.Serializable

@Serializable
enum class DiaryResult(val apiValue: String) {
    DID("EXPERIENCED"), MISSED("NOT_EXPERIENCED");

    companion object {
        fun fromApi(value: String): DiaryResult =
            requireNotNull(entries.firstOrNull { it.apiValue == value }) { "알 수 없는 경험 결과입니다." }
    }
}

/** 앱 내부 요청 모델. 서버 JSON/Multipart 필드는 실제 API 명세에 맞춰 Repository에서 변환한다. */
data class CreateDiaryRequest(
    val draftId: String,
    val draftStartedAt: String,
    val questId: String,
    val questTitle: String,
    val location: String,
    val result: DiaryResult,
    val photoUri: String,
    val memo: String = "",
)

@Serializable
data class SavedDiary(
    val id: String,
    val questId: String,
    val questTitle: String,
    val location: String,
    val result: DiaryResult,
    val photoUri: String,
    val memo: String,
    val savedAt: String,
)
