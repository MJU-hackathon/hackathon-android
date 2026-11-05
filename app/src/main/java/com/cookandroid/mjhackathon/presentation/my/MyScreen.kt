package com.cookandroid.mjhackathon.presentation.my

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
fun MyRoute(
    paddingValues: PaddingValues,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
    )
}

@Composable
fun MyScreen(
    uiState: MyUiState,
    modifier: Modifier = Modifier,
) {
    Text(
        text = uiState.text,
        modifier = modifier,
    )
}

@Preview(name = "My - Default", showBackground = true)
@Composable
private fun MyScreenPreview() {
    MjHackathonTheme {
        MyScreen(uiState = MyUiState())
    }
}
