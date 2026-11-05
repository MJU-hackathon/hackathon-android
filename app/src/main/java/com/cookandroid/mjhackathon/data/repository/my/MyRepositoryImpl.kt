package com.cookandroid.mjhackathon.data.repository.my

import com.cookandroid.mjhackathon.data.model.my.DiaryCollection
import com.cookandroid.mjhackathon.data.model.my.DiaryMonthGroup
import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult
import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary
import com.cookandroid.mjhackathon.data.network.toApiException
import com.cookandroid.mjhackathon.data.service.my.MyService
import jakarta.inject.Inject
import retrofit2.HttpException

class MyRepositoryImpl @Inject constructor(
    private val myService: MyService,
) : MyRepository {
    override suspend fun getDiaries(locale: String?): DiaryCollection {
        val response = try { myService.getDiaries(locale).data }
        catch (error: HttpException) { throw error.toApiException() }
        return DiaryCollection(
            totalCount = response.totalCount,
            experiencedCount = response.experiencedCount,
            notExperiencedCount = response.notExperiencedCount,
            groups = response.groups.map { group ->
                DiaryMonthGroup(group.yearMonth, group.diaries.map { diary ->
                    SavedDiary(
                        id = diary.diaryId.toString(), questId = diary.questId.toString(),
                        questTitle = diary.title, location = diary.placeName,
                        result = DiaryResult.fromApi(diary.outcome), photoUri = diary.photoUrl,
                        memo = diary.memo.orEmpty(), savedAt = diary.recordedAt,
                    )
                })
            },
        )
    }
}
