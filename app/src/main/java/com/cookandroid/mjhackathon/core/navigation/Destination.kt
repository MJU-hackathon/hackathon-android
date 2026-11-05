package com.cookandroid.mjhackathon.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data class Detail(val questId: String)

@Serializable
data class Profile(val isInitialSetup: Boolean = false)

@Serializable
data class PostDaily(
    val questId: String, val photoUri: String,
    val draftId: String, val draftStartedAt: String,
)

@Serializable
data object My

@Serializable
data object Splash
