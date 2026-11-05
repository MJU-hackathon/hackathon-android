package com.cookandroid.mjhackathon.presentation.detail

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
import com.cookandroid.mjhackathon.presentation.home.HomeScreen

@Composable
fun DetailRoute(
    paddingValues: PaddingValues,
    viewModel: DetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit //navigate 함수는 추후에 바꿔야 함.
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
        onNavigateToBack = navigateToBack
    )
}

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit
) {
    Text("test")
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MjHackathonTheme {
        DetailScreen(uiState = DetailUiState(), onNavigateToBack = {})
    }
}
