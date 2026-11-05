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
import java.time.OffsetDateTime
import java.io.IOException
import com.cookandroid.mjhackathon.data.network.ApiException
import com.cookandroid.mjhackathon.data.repository.postDaily.DiaryPhotoException

@HiltViewModel
class PostDailyViewModel @Inject constructor(
    private val postDailyRepository: PostDailyRepository,
    private val detailRepository: DetailRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<PostDaily>()
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
                UUID.fromString(route.draftId)
                OffsetDateTime.parse(route.draftStartedAt)
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
                        draftId = route.draftId, draftStartedAt = route.draftStartedAt, questId = route.questId,
                        questTitle = state.questTitle, location = state.location,
                        result = requireNotNull(state.result), photoUri = route.photoUri, memo = state.memo,
                    ),
                )
                savedStateHandle["diary_saved_id"] = saved.id
                // 서버 저장 성공 후 촬영 임시 파일만 정리한다. 목록은 서버에서 다시 조회한다.
                try { postDailyRepository.discardTemporaryPhoto(route.photoUri) }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { /* 저장 성공은 유지하며 캐시 정리 실패로 재저장하지 않는다. */ }
                _uiState.update { it.copy(isSaving = false, savedDiaryId = saved.id) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                val code = when (error) {
                    is ApiException -> error.code ?: if (error.httpStatus == 413) "FILE_TOO_LARGE" else null
                    is DiaryPhotoException -> error.code
                    else -> null
                }
                _uiState.update { it.copy(isSaving = false, saveError = error.diaryMessage(),
                    saveBlocked = code in setOf("QUEST_NOT_AVAILABLE", "ALREADY_RECORDED", "QUEST_NOT_FOUND",
                        "PHOTO_REQUIRED", "INVALID_PHOTO", "FILE_TOO_LARGE")) }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
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

private fun Exception.diaryMessage(): String {
    val code = when (this) {
        is ApiException -> code ?: if (httpStatus == 413) "FILE_TOO_LARGE" else null
        is DiaryPhotoException -> code
        else -> null
    }
    return when (code) {
        "PHOTO_REQUIRED" -> "사진을 확인할 수 없어요. 작성 취소 후 다시 촬영해 주세요."
        "OUTCOME_REQUIRED" -> "경험 결과를 선택해 주세요."
        "MEMO_TOO_LONG" -> "메모는 50자까지 입력할 수 있어요."
        "INVALID_PHOTO", "FILE_TOO_LARGE" -> "사진은 JPEG·PNG·HEIC 형식의 10MB 이하 파일이어야 해요. 작성 취소 후 다시 촬영해 주세요."
        "QUEST_NOT_FOUND" -> "퀘스트를 찾을 수 없어요. 홈에서 다시 확인해 주세요."
        "PROFILE_NOT_FOUND" -> "프로필을 먼저 저장해 주세요."
        "QUEST_NOT_AVAILABLE" -> "이미 패스했거나 기록할 수 없는 퀘스트예요. 홈에서 다시 확인해 주세요."
        "ALREADY_RECORDED" -> "이미 다른 작성 기록으로 저장된 퀘스트예요. 홈에서 다시 확인해 주세요."
        "USER_KEY_REQUIRED" -> "사용자 정보를 확인할 수 없어요. 다시 시도해 주세요."
        "INVALID_INPUT" -> "기록 정보를 확인할 수 없어요. 작성 취소 후 다시 촬영해 주세요."
        "UPLOAD_FAILED" -> "사진 업로드에 실패했어요. 다시 저장해 주세요."
        else -> if (this is IOException) "서버에 연결할 수 없어요. 입력한 내용은 유지돼요. 다시 저장해 주세요."
            else "저장하지 못했어요. 입력한 내용은 유지돼요. 다시 시도해 주세요."
    }
}
