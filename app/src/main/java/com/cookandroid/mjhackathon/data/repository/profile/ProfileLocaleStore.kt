package com.cookandroid.mjhackathon.data.repository.profile

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ProfileLocaleStore @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences("profile_settings", Context.MODE_PRIVATE)
    fun getLocale(): String = preferences.getString("locale", "ko")
        ?.takeIf { it in setOf("ko", "en", "vi", "zh", "ja") } ?: "ko"

    fun saveLocale(locale: String) {
        preferences.edit().putString("locale", locale).apply()
    }
}
