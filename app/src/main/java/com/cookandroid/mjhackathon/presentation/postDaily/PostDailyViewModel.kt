package com.cookandroid.mjhackathon.presentation.postDaily

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cookandroid.mjhackathon.core.navigation.PostDaily
import com.cookandroid.mjhackathon.data.model.postDaily.CreateDiaryRequest
import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult
import com.cookandroid.mjhackathon.data.repository.detail.DetailRepository
import com.cookandroid.mjhackathon.data.repository.postDaily.PostDailyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class PostDailyViewModel @Inject constructor(
    private val postDailyRepository: PostDailyRepository,
    private val detailRepository: DetailRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<PostDaily>()
    private val requestId = savedStateHandle.get<String>("diary_request_id")
        ?: UUID.randomUUID().toString().also { savedStateHandle["diary_request_id"] = it }
    private val _uiState = MutableStateFlow(
        PostDailyUiState(
            questId = route.questId,
            photoUri = route.photoUri,
            memo = limitDiaryMemo(savedStateHandle.get<String>("diary_memo").orEmpty()),
            result = savedStateHandle.get<String>("diary_result")?.let { name ->
                DiaryResult.entries.firstOrNull { it.name == name }
            },
            savedDiaryId = savedStateHandle.get<String>("diary_saved_id"),
        ),
    )
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init { loadQuest() }

    fun loadQuest() {
        if (_uiState.value.isBusy || _uiState.value.savedDiaryId != null) return
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, loadError = null) }
        loadJob = viewModelScope.launch {
            try {
                val quest = detailRepository.getQuestDetail(route.questId)
                check(quest.id == route.questId)
                val hasPhoto = postDailyRepository.isPhotoAvailable(route.photoUri)
                _uiState.update {
                    it.copy(questTitle = quest.title, location = quest.placeName,
                        hasPhoto = hasPhoto, isLoading = false, loadError = null)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, loadError = "퀘스트 정보를 불러오지 못했어요.") }
            }
        }
    }

    fun selectResult(result: DiaryResult) {
        if (_uiState.value.isBusy || _uiState.value.savedDiaryId != null) return
        savedStateHandle["diary_result"] = result.name
        _uiState.update { it.copy(result = result, saveError = null) }
    }

    fun updateMemo(value: String) {
        if (_uiState.value.isBusy || _uiState.value.savedDiaryId != null) return
        val limited = limitDiaryMemo(value)
        savedStateHandle["diary_memo"] = limited
        _uiState.update { it.copy(memo = limited, saveError = null) }
    }

    fun onPhotoLoadError() { _uiState.update { it.copy(hasPhoto = false) } }

    fun saveDiary() {
        val state = _uiState.value
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            try {
                val saved = postDailyRepository.saveDiary(
                    CreateDiaryRequest(
                        requestId = requestId, questId = route.questId,
                        questTitle = state.questTitle, location = state.location,
                        result = requireNotNull(state.result), photoUri = route.photoUri, memo = state.memo,
                    ),
                )
                savedStateHandle["diary_saved_id"] = saved.id
                // 저장된 사진은 Repository가 별도 보관한다. 촬영용 임시 파일만 정리한다.
                try { postDailyRepository.discardTemporaryPhoto(route.photoUri) }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { /* 저장 성공은 유지하며 캐시 정리 실패로 재저장하지 않는다. */ }
                _uiState.update { it.copy(isSaving = false, savedDiaryId = saved.id) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false, saveError = "저장하지 못했어요. 다시 시도해 주세요.") }
            }
        }
    }

    fun cancelWriting() {
        if (_uiState.value.isBusy || _uiState.value.savedDiaryId != null || _uiState.value.isCancelled) return
        loadJob?.cancel()
        _uiState.update { it.copy(isCancelling = true) }
        viewModelScope.launch {
            try { postDailyRepository.discardTemporaryPhoto(route.photoUri) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { /* 캐시 정리 실패가 작성 취소를 막지 않게 한다. */ }
            _uiState.update { it.copy(isCancelling = false, isCancelled = true) }
        }
    }
}
