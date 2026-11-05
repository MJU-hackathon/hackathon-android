package com.cookandroid.mjhackathon.presentation.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme

@Composable
fun HomeRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToDetail: () -> Unit //navigate 함수는 추후에 바꿔야 함.
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
        onNavigateToDetail = navigateToDetail
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onNavigateToDetail: () -> Unit
) {
    Text("test")
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MjHackathonTheme {
        HomeScreen(uiState = HomeUiState(), onNavigateToDetail = {})
    }
}
