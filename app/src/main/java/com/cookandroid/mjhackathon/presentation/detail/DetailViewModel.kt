package com.cookandroid.mjhackathon.presentation.detail

import androidx.lifecycle.ViewModel
import com.cookandroid.mjhackathon.data.repository.detail.DetailRepository
import com.cookandroid.mjhackathon.data.repository.home.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val detailRepository: DetailRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()
}
