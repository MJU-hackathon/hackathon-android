package com.cookandroid.mjhackathon.presentation.my

import androidx.lifecycle.ViewModel
import com.cookandroid.mjhackathon.data.repository.my.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MyViewModel @Inject constructor(
    private val myRepository: MyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyUiState())
    val uiState = _uiState.asStateFlow()
}
