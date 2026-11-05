package com.cookandroid.mjhackathon.data.model.postDaily

import kotlinx.serialization.Serializable

@Serializable
enum class DiaryResult { DID, MISSED }

/** 앱 내부 요청 모델. 서버 JSON/Multipart 필드는 실제 API 명세에 맞춰 Repository에서 변환한다. */
data class CreateDiaryRequest(
    val requestId: String,
    val questId: String,
    val questTitle: String,
    val location: String,
    val result: DiaryResult,
    val photoUri: String,
    val memo: String,
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
