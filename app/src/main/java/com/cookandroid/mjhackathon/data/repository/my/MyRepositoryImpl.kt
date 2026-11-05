package com.cookandroid.mjhackathon.data.repository.my

import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary
import com.cookandroid.mjhackathon.data.repository.postDaily.PostDailyRepository
import jakarta.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val postDailyRepository: PostDailyRepository,
) : MyRepository {
    override suspend fun getDiaries(): List<SavedDiary> =
        postDailyRepository.getSavedDiaries()

    // API 연결 지점: 실제 일기 목록 Service 응답을 SavedDiary로 변환해 반환한다.
    // 현재는 앞서 구현한 일기 작성 Repository의 로컬 저장 기록을 조회한다.
}
