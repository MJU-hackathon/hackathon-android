package com.cookandroid.mjhackathon.data.repository.my

import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary

interface MyRepository {
    suspend fun getDiaries(): List<SavedDiary>
}
