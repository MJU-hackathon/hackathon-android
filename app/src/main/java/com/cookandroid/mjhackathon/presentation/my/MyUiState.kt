package com.cookandroid.mjhackathon.presentation.my

import com.cookandroid.mjhackathon.data.model.my.DiaryCollection
import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class MyDiaryEntry(val diary: SavedDiary, val displayDate: String)

data class MyDiaryMonth(val key: String, val title: String, val entries: List<MyDiaryEntry>)

data class MyUiState(
    val isLoading: Boolean = true,
    val months: List<MyDiaryMonth> = emptyList(),
    val errorMessage: String? = null,
    val totalCount: Int = 0,
    val didCount: Int = 0,
    val missedCount: Int = 0,
    val profileRequired: Boolean = false,
) {
    companion object {
        /** 서버의 월별 분류·순서·집계를 유지하고 표시용 날짜만 변환한다. */
        fun fromCollection(collection: DiaryCollection): MyUiState {
            val seoul = ZoneId.of("Asia/Seoul")
            val format = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            return MyUiState(
                isLoading = false, totalCount = collection.totalCount,
                didCount = collection.experiencedCount, missedCount = collection.notExperiencedCount,
                months = collection.groups.map { group ->
                    val month = YearMonth.parse(group.yearMonth)
                    MyDiaryMonth(
                        key = group.yearMonth, title = "${month.year}년 ${month.monthValue}월",
                        entries = group.diaries.map { diary ->
                            val date = OffsetDateTime.parse(diary.savedAt).atZoneSameInstant(seoul)
                            MyDiaryEntry(diary, date.format(format))
                        },
                    )
                },
            )
        }
    }
}
