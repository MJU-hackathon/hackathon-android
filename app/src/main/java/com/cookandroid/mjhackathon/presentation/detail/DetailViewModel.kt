package com.cookandroid.mjhackathon.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cookandroid.mjhackathon.core.navigation.Detail
import com.cookandroid.mjhackathon.data.repository.detail.DetailRepository
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
class DetailViewModel @Inject constructor(
    private val detailRepository: DetailRepository,
    savedStateHandle: SavedStateHandle,
    private val localeStore: ProfileLocaleStore,
) : ViewModel() {
    val questId: String = savedStateHandle.toRoute<Detail>().questId
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init { loadQuest() }

    fun loadQuest() {
        if (_uiState.value.isPassing) return
        loadJob?.cancel()
        _uiState.value = DetailUiState()
        loadJob = viewModelScope.launch {
            try {
                require(questId.isNotBlank())
                val quest = detailRepository.getQuestDetail(questId, localeStore.getLocale())
                check(quest.id == questId) { "퀘스트 정보가 일치하지 않습니다." }
                _uiState.value = DetailUiState(isLoading = false, quest = quest)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.value = DetailUiState(isLoading = false, loadError = error.detailMessage())
            }
        }
    }

    fun passQuest() {
        val state = _uiState.value
        if (!state.canAct) return
        _uiState.update { it.copy(isPassing = true, passError = null) }
        viewModelScope.launch {
            try {
                detailRepository.passQuest(questId)
                _uiState.update { it.copy(isPassing = false, passedQuestId = questId) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(passError = error.detailMessage(), actionsUnavailable =
                        error is ApiException && error.code in setOf("QUEST_NOT_AVAILABLE", "QUEST_NOT_FOUND"))
                }
            } finally {
                _uiState.update { it.copy(isPassing = false) }
            }
        }
    }
}

private fun Exception.detailMessage(): String = when (this) {
    is ApiException -> when (code) {
        "QUEST_NOT_FOUND" -> "퀘스트를 찾을 수 없어요. 홈에서 다시 확인해 주세요."
        "PROFILE_NOT_FOUND" -> "프로필을 먼저 저장해 주세요."
        "QUEST_NOT_AVAILABLE" -> "이미 처리했거나 날짜가 지난 퀘스트예요. 홈에서 다시 확인해 주세요."
        "USER_KEY_REQUIRED" -> "사용자 정보를 확인할 수 없어요. 다시 시도해 주세요."
        "UNSUPPORTED_LOCALE" -> "프로필의 사용 언어를 확인해 주세요."
        "INVALID_INPUT" -> "요청 정보를 확인할 수 없어요. 홈에서 다시 선택해 주세요."
        else -> "요청을 처리하지 못했어요. 잠시 후 다시 시도해 주세요."
    }
    is IOException -> "서버에 연결할 수 없어요. 잠시 후 다시 시도해 주세요."
    is IllegalArgumentException -> "퀘스트 정보를 확인할 수 없어요. 홈에서 다시 선택해 주세요."
    else -> "요청을 처리하지 못했어요. 다시 시도해 주세요."
}
