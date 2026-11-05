package com.cookandroid.mjhackathon.presentation.postDaily

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
fun PostDailyRoute(
    paddingValues: PaddingValues,
    viewModel: PostDailyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostDailyScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
    )
}

@Composable
fun PostDailyScreen(
    uiState: PostDailyUiState,
    modifier: Modifier = Modifier,
) {
    Text(
        text = uiState.text,
        modifier = modifier,
    )
}

@Preview(name = "PostDaily - Default", showBackground = true)
@Composable
private fun PostDailyScreenPreview() {
    MjHackathonTheme {
        PostDailyScreen(uiState = PostDailyUiState())
    }
}
