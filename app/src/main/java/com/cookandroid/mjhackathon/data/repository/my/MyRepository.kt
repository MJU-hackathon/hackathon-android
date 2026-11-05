package com.cookandroid.mjhackathon.data.repository.my

import com.cookandroid.mjhackathon.data.model.my.DiaryCollection

interface MyRepository {
    suspend fun getDiaries(locale: String? = null): DiaryCollection
}
