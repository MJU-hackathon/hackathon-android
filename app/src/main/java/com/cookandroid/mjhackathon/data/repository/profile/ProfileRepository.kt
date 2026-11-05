package com.cookandroid.mjhackathon.data.repository.profile

import com.cookandroid.mjhackathon.data.dto.profile.response.ExampleResponse
import com.cookandroid.mjhackathon.data.dto.profile.request.SaveProfileRequest
import com.cookandroid.mjhackathon.data.dto.profile.response.ProfileResponse

interface ProfileRepository {
    suspend fun saveProfile(request: SaveProfileRequest): ProfileResponse
    suspend fun getProfiles(): List<ExampleResponse>
}
