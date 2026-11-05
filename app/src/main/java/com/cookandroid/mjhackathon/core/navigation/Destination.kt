package com.cookandroid.mjhackathon.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object Detail

@Serializable
data class Profile(val isInitialSetup: Boolean = false)

@Serializable
data object PostDaily

@Serializable
data object My

@Serializable
data object Splash
