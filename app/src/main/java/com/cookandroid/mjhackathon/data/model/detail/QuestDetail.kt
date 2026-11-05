package com.cookandroid.mjhackathon.data.model.detail

/** 서버 상세 응답을 화면에서 사용하는 값으로 변환한 모델. */
data class QuestDetail(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val steps: List<String>,
    val placeName: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val imageUrl: String? = null,
    val estimatedMinutes: Int = 0,
    val reason: String = "",
    val locale: String = "ko",
    val status: String = "AVAILABLE",
) {
    val hasMapCoordinates: Boolean
        get() = latitude != null && longitude != null &&
            latitude.isFinite() && longitude.isFinite() &&
            latitude in -90.0..90.0 && longitude in -180.0..180.0
}
