package com.cookandroid.mjhackathon.data.repository.profile

import com.cookandroid.mjhackathon.data.service.profile.ProfileService
import jakarta.inject.Inject
import com.cookandroid.mjhackathon.data.dto.profile.request.SaveProfileRequest
import com.cookandroid.mjhackathon.data.dto.profile.response.ProfileResponse

class ProfileRepositoryImpl @Inject constructor(
    private val profileService: ProfileService,
    private val localeStore: ProfileLocaleStore,
) : ProfileRepository {
    override suspend fun saveProfile(request: SaveProfileRequest): ProfileResponse =
        profileService.saveProfile(request).data.also { localeStore.saveLocale(it.locale) }

//    override suspend fun getProfiles(): List<ExampleResponse> =
//        profileService.getExampleData().data
}
