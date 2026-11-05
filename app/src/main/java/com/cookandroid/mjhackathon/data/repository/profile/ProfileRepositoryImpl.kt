package com.cookandroid.mjhackathon.data.repository.profile

import com.cookandroid.mjhackathon.data.dto.profile.response.ExampleResponse
import com.cookandroid.mjhackathon.data.service.profile.ProfileService
import jakarta.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService,
) : ProfileRepository {
    override suspend fun getProfiles(): List<ExampleResponse> =
        profileService.getExampleData().data
}
