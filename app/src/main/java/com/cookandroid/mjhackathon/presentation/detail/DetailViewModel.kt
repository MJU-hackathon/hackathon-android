package com.cookandroid.mjhackathon.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cookandroid.mjhackathon.core.navigation.Detail
import com.cookandroid.mjhackathon.data.repository.detail.DetailRepository
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
                val quest = detailRepository.getQuestDetail(questId)
                check(quest.id == questId) { "퀘스트 정보가 일치하지 않습니다." }
                _uiState.value = DetailUiState(isLoading = false, quest = quest)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.value = DetailUiState(isLoading = false, loadError = "퀘스트를 불러오지 못했어요.")
            }
        }
    }

    fun passQuest() {
        val state = _uiState.value
        if (state.quest == null || state.isPassing || state.passedQuestId != null) return
        _uiState.update { it.copy(isPassing = true, passError = null) }
        viewModelScope.launch {
            try {
                detailRepository.passQuest(questId)
                _uiState.update { it.copy(isPassing = false, passedQuestId = questId) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(isPassing = false, passError = "패스하지 못했어요. 다시 시도해 주세요.") }
            }
        }
    }
}
