package com.cookandroid.mjhackathon.presentation.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookandroid.mjhackathon.data.repository.my.MyRepository
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
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null

    /** Route의 ON_RESUME 및 재시도에서 호출해 저장 직후/재진입 시 목록을 갱신한다. */
    fun refresh() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadJob = viewModelScope.launch {
            try {
                _uiState.value = MyUiState.fromDiaries(myRepository.getDiaries())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "일기를 불러오지 못했어요. 다시 시도해 주세요.") }
            }
        }
    }
}
