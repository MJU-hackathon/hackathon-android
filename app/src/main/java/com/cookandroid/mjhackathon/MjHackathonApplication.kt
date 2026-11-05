package com.cookandroid.mjhackathon

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MjHackathonApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
