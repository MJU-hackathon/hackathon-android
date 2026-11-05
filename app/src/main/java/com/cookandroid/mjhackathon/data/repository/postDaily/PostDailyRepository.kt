package com.cookandroid.mjhackathon.data.repository.postDaily

import com.cookandroid.mjhackathon.data.model.postDaily.CreateDiaryRequest
import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary

interface PostDailyRepository {
    suspend fun isPhotoAvailable(photoUri: String): Boolean
    suspend fun saveDiary(request: CreateDiaryRequest): SavedDiary
    suspend fun discardTemporaryPhoto(photoUri: String)
}
