package com.cookandroid.mjhackathon.data.dto.my.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiaryListResponse(
    @SerialName("totalCount") val totalCount: Int,
    @SerialName("experiencedCount") val experiencedCount: Int,
    @SerialName("notExperiencedCount") val notExperiencedCount: Int,
    @SerialName("groups") val groups: List<DiaryGroupResponse>,
)

@Serializable
data class DiaryGroupResponse(
    @SerialName("yearMonth") val yearMonth: String,
    @SerialName("diaries") val diaries: List<DiaryEntryResponse>,
)

@Serializable
data class DiaryEntryResponse(
    @SerialName("diaryId") val diaryId: Long,
    @SerialName("questId") val questId: Long,
    @SerialName("title") val title: String,
    @SerialName("placeName") val placeName: String,
    @SerialName("photoUrl") val photoUrl: String,
    @SerialName("outcome") val outcome: String,
    @SerialName("memo") val memo: String? = null,
    @SerialName("recordedAt") val recordedAt: String,
)
