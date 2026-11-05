package com.cookandroid.mjhackathon.data.repository.location

data class QuestCoordinates(val latitude: Double, val longitude: Double)

interface CurrentLocationProvider {
    suspend fun getCurrentLocation(): QuestCoordinates
}
