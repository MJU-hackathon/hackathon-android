package com.cookandroid.mjhackathon.presentation.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookandroid.mjhackathon.data.repository.my.MyRepository
import com.cookandroid.mjhackathon.data.repository.profile.ProfileLocaleStore
import com.cookandroid.mjhackathon.data.network.ApiException
import java.io.IOException
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyViewModel @Inject constructor(
    private val myRepository: MyRepository,
    private val localeStore: ProfileLocaleStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null

    /** Route의 ON_RESUME 및 재시도에서 호출해 저장 직후/재진입 시 목록을 갱신한다. */
    fun refresh() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, errorMessage = null, profileRequired = false) }
        loadJob = viewModelScope.launch {
            try {
                _uiState.value = MyUiState.fromCollection(myRepository.getDiaries(localeStore.getLocale()))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = error.diaryListMessage(),
                    profileRequired = error is ApiException && error.code == "PROFILE_NOT_FOUND") }
            }
        }
    }
}

private fun Exception.diaryListMessage(): String = when (this) {
    is ApiException -> when (code) {
        "PROFILE_NOT_FOUND" -> "프로필을 먼저 저장해 주세요."
        "USER_KEY_REQUIRED" -> "사용자 정보를 확인할 수 없어요. 다시 시도해 주세요."
        "UNSUPPORTED_LOCALE" -> "프로필의 사용 언어를 확인해 주세요."
        "INVALID_INPUT" -> "요청 정보를 확인할 수 없어요. 다시 시도해 주세요."
        else -> "일기를 불러오지 못했어요. 잠시 후 다시 시도해 주세요."
    }
    is IOException -> "서버에 연결할 수 없어요. 잠시 후 다시 시도해 주세요."
    else -> "일기를 불러오지 못했어요. 다시 시도해 주세요."
}
