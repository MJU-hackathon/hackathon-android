package com.cookandroid.mjhackathon.data.model.my

import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary

/** 월별 묶음과 집계 모두 서버에서 확정한 값이다. */
data class DiaryCollection(
    val totalCount: Int,
    val experiencedCount: Int,
    val notExperiencedCount: Int,
    val groups: List<DiaryMonthGroup>,
)

data class DiaryMonthGroup(val yearMonth: String, val diaries: List<SavedDiary>)
