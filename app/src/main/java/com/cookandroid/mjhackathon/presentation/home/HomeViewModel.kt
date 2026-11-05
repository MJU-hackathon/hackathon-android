package com.cookandroid.mjhackathon.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookandroid.mjhackathon.data.repository.home.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.time.ZoneId

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val initialState = HomeUiState.mock()
    private val today = ZonedDateTime.now(SEOUL).toLocalDate().toString()
    private val resolvedIds = if (savedStateHandle.get<String>(QUEST_DAY) == today) {
        savedStateHandle.get<ArrayList<String>>(RESOLVED_IDS).orEmpty()
    } else {
        savedStateHandle[QUEST_DAY] = today
        savedStateHandle[RESOLVED_IDS] = arrayListOf<String>()
        emptyList()
    }
    private val _uiState = MutableStateFlow(
        initialState.copy(quests = initialState.quests.filterNot { it.id in resolvedIds }),
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>(QUEST_RESULT_KEY, null)
                .filterNotNull().collect { questId ->
                    resolveQuest(questId)
                    savedStateHandle.set<String?>(QUEST_RESULT_KEY, null)
                }
        }
        // 목업에서도 한국 시간의 날짜가 바뀌면 오늘의 세 퀘스트를 다시 제공한다.
        viewModelScope.launch {
            while (true) {
                val now = ZonedDateTime.now(SEOUL)
                val midnight = now.toLocalDate().plusDays(1).atStartOfDay(SEOUL)
                delay(Duration.between(now, midnight).toMillis().coerceAtLeast(1L))
                savedStateHandle[QUEST_DAY] = ZonedDateTime.now(SEOUL).toLocalDate().toString()
                savedStateHandle[RESOLVED_IDS] = arrayListOf<String>()
                _uiState.value = initialState
            }
        }
    }

    /** 완료/패스 성공 결과를 받았을 때만 호출한다. 상세 진입 자체로 제거하지 않는다. */
    fun resolveQuest(questId: String) {
        if (_uiState.value.quests.none { it.id == questId }) return
        val ids = savedStateHandle.get<ArrayList<String>>(RESOLVED_IDS).orEmpty()
        savedStateHandle[RESOLVED_IDS] = ArrayList(ids + questId)
        _uiState.update { state -> state.copy(quests = state.quests.filterNot { it.id == questId }) }
    }

    companion object {
        const val QUEST_RESULT_KEY = "home_resolved_quest_id"
        const val SELECTED_QUEST_KEY = "home_selected_quest_id"
        private const val RESOLVED_IDS = "home_mock_resolved_ids"
        private const val QUEST_DAY = "home_mock_quest_day"
        private val SEOUL = ZoneId.of("Asia/Seoul")
    }
}
