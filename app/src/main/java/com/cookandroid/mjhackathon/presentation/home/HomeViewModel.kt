package com.cookandroid.mjhackathon.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookandroid.mjhackathon.data.network.ApiException
import com.cookandroid.mjhackathon.data.repository.home.HomeRepository
import com.cookandroid.mjhackathon.data.repository.location.LocationUnavailableException
import com.cookandroid.mjhackathon.data.repository.profile.ProfileLocaleStore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val savedStateHandle: SavedStateHandle,
    private val localeStore: ProfileLocaleStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var questDay: LocalDate = LocalDate.now(SEOUL)
    private val resolvedIds = mutableSetOf<String>()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>(QUEST_RESULT_KEY, null)
                .filterNotNull().collect { questId ->
                    resolveQuest(questId)
                    savedStateHandle.set<String?>(QUEST_RESULT_KEY, null)
                }
        }
    }

    fun loadQuests() {
        if (loadJob?.isActive == true) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null, profileRequired = false) }
        loadJob = viewModelScope.launch {
            val today = LocalDate.now(SEOUL)
            if (questDay != today) {
                questDay = today
                resolvedIds.clear()
                _uiState.update { it.copy(quests = emptyList()) }
            }
            try {
                val quests = homeRepository.getOrCreateTodayQuests(localeStore.getLocale())
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        quests = quests.filterNot { it.questId.toString() in resolvedIds }.map {
                            HomeQuest(
                                id = it.questId.toString(), title = it.title, category = it.category,
                                location = it.placeName, estimatedMinutes = it.estimatedMinutes,
                                imageUrl = it.imageUrl, reason = it.reason,
                            )
                        },
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.homeMessage(),
                        profileRequired = error is ApiException && error.code == "PROFILE_NOT_FOUND")
                }
            }
        }
    }

    /** 상세/일기에서 성공 결과를 받은 항목만 제거하며 새 퀘스트로 보충하지 않는다. */
    fun resolveQuest(questId: String) {
        resolvedIds.add(questId)
        _uiState.update { it.copy(quests = it.quests.filterNot { quest -> quest.id == questId }) }
    }

    companion object {
        const val QUEST_RESULT_KEY = "home_resolved_quest_id"
        const val SELECTED_QUEST_KEY = "home_selected_quest_id"
        private val SEOUL = ZoneId.of("Asia/Seoul")
    }
}

private fun Exception.homeMessage(): String = when {
    this is LocationUnavailableException -> "현재 위치를 확인할 수 없어요. 잠시 후 다시 시도해 주세요."
    this is ApiException -> when (code) {
        "PROFILE_NOT_FOUND" -> "퀘스트를 시작하려면 프로필을 먼저 저장해 주세요."
        "USER_KEY_REQUIRED" -> "사용자 정보를 확인할 수 없어요. 다시 시도해 주세요."
        "UNSUPPORTED_LOCALE" -> "프로필의 사용 언어를 확인해 주세요."
        "INVALID_INPUT" -> "요청 정보를 확인할 수 없어요. 다시 시도해 주세요."
        else -> "퀘스트를 불러오지 못했어요. 잠시 후 다시 시도해 주세요."
    }
    this is IOException -> "서버에 연결할 수 없어요. 잠시 후 다시 시도해 주세요."
    else -> "퀘스트를 불러오지 못했어요. 다시 시도해 주세요."
}
