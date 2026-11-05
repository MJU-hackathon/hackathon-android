package com.cookandroid.mjhackathon.data.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.UUID
import java.io.IOException

/** 서버가 사용자를 식별하는 키. 프로필 수정과 이후 API에서도 같은 값을 사용한다. */
@Singleton
class UserKeyStore @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences("user_identity", Context.MODE_PRIVATE)

    @Synchronized
    fun getOrCreate(): String {
        val stored = preferences.getString("user_key", null)
        if (!stored.isNullOrBlank() && stored.length <= 64) return stored
        val created = UUID.randomUUID().toString()
        if (!preferences.edit().putString("user_key", created).commit()) {
            throw IOException("사용자 정보를 저장하지 못했어요. 다시 시도해 주세요.")
        }
        return created
    }
}
