package com.cookandroid.mjhackathon.presentation.my

import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult
import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class MyDiaryEntry(val diary: SavedDiary, val displayDate: String)

data class MyDiaryMonth(
    val key: String,
    val title: String,
    val entries: List<MyDiaryEntry>,
)

data class MyUiState(
    val isLoading: Boolean = true,
    val months: List<MyDiaryMonth> = emptyList(),
    val errorMessage: String? = null,
) {
    val totalCount: Int get() = months.sumOf { it.entries.size }
    val didCount: Int get() = months.sumOf { month -> month.entries.count { it.diary.result == DiaryResult.DID } }
    val missedCount: Int get() = months.sumOf { month -> month.entries.count { it.diary.result == DiaryResult.MISSED } }

    companion object {
        fun fromDiaries(diaries: List<SavedDiary>) = MyUiState(isLoading = false, months = groupDiaryMonths(diaries))
    }
}

/** 원문의 메모는 변경하지 않고, 날짜를 한국 시간으로 변환하여 최신순으로 묶는다. */
internal fun groupDiaryMonths(diaries: List<SavedDiary>): List<MyDiaryMonth> {
    val seoul = ZoneId.of("Asia/Seoul")
    val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return diaries.map { it to OffsetDateTime.parse(it.savedAt).toInstant() }
        .sortedByDescending { it.second }
        .groupBy { YearMonth.from(it.second.atZone(seoul)) }
        .map { (month, records) ->
            MyDiaryMonth(
                key = month.toString(),
                title = "${month.year}년 ${month.monthValue}월",
                entries = records.map { (diary, instant) ->
                    MyDiaryEntry(diary, instant.atZone(seoul).format(dateFormat))
                },
            )
        }
}
