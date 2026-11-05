package com.cookandroid.mjhackathon.data.repository.profile

import com.cookandroid.mjhackathon.data.dto.profile.response.ExampleResponse

interface ProfileRepository {
    suspend fun getProfiles(): List<ExampleResponse>
}
