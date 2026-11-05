package com.cookandroid.mjhackathon.data.model.detail

/** UI와 Repository 사이의 모델. 서버 DTO가 확정되면 Repository에서 이 타입으로 변환한다. */
data class QuestDetail(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val steps: List<String>,
    val placeName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = null,
)
