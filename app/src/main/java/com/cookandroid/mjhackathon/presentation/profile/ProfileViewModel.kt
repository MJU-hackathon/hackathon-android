package com.cookandroid.mjhackathon.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookandroid.mjhackathon.data.repository.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    fun updateState(state: ProfileState) {
        if (_uiState.value.isSaving || _uiState.value.isSaved) return
        _uiState.value = state.copy(isSaving = false, isSaved = false, saveErrorMessage = null)
    }

    fun saveProfile() {
        val state = _uiState.value
        if (!state.canSave || state.isSaving || state.isSaved) return
        _uiState.value = state.copy(isSaving = true, saveErrorMessage = null)
        viewModelScope.launch {
            try {
                val profile = profileRepository.saveProfile(state.toSaveRequest())
                _uiState.update {
                    it.copy(
                        nickname = profile.nickname,
                        language = profileLocales.entries.firstOrNull { entry -> entry.value == profile.locale }?.key ?: state.language,
                        birthday = profile.birthDate,
                        stayStart = profile.stayStartDate,
                        stayEnd = profile.stayEndDate,
                        interests = profile.interests.mapNotNull { code ->
                            profileInterests.entries.firstOrNull { entry -> entry.value == code }?.key
                        }.toSet(),
                        isSaved = true,
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update { it.copy(saveErrorMessage = error.toProfileMessage()) }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun consumeSaveSuccess() {
        _uiState.update { it.copy(isSaved = false) }
    }
}

private fun Exception.toProfileMessage(): String {
    if (this is IOException) return "서버에 연결할 수 없어요. 인터넷 연결을 확인하고 다시 시도해 주세요."
    val code = if (this is HttpException) runCatching {
        Json.parseToJsonElement(response()?.errorBody()?.string().orEmpty())
            .jsonObject["code"]?.jsonPrimitive?.content
    }.getOrNull() else null
    return when (code) {
        "INVALID_INPUT" -> "입력한 정보를 다시 확인해 주세요."
        "INVALID_NICKNAME" -> "닉네임은 2~12자로 입력해 주세요."
        "INVALID_STAY_PERIOD" -> "종료일은 시작일 이후로 선택해 주세요."
        "INTEREST_REQUIRED" -> "관심사를 1개 이상 선택해 주세요."
        "UNSUPPORTED_LOCALE" -> "지원하는 사용 언어를 선택해 주세요."
        "USER_KEY_REQUIRED" -> "사용자 정보를 확인하지 못했어요. 다시 시도해 주세요."
        else -> "정보를 저장하지 못했어요. 잠시 후 다시 시도해 주세요."
    }
}
