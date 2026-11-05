package com.cookandroid.mjhackathon.presentation.postDaily

import androidx.lifecycle.ViewModel
import com.cookandroid.mjhackathon.data.repository.postDaily.PostDailyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class PostDailyViewModel @Inject constructor(
    private val postDailyRepository: PostDailyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDailyUiState())
    val uiState = _uiState.asStateFlow()
}
