package com.cookandroid.mjhackathon.presentation.postDaily

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.component.MjAppBar
import com.cookandroid.mjhackathon.core.designsystem.component.MjAppBarType
import com.cookandroid.mjhackathon.core.designsystem.component.MjButton
import com.cookandroid.mjhackathon.core.designsystem.component.MjQuestSummary
import com.cookandroid.mjhackathon.core.designsystem.component.MjResultSelector
import com.cookandroid.mjhackathon.core.designsystem.component.MjResultSelectorType
import com.cookandroid.mjhackathon.core.designsystem.component.MjTextField
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult

@Composable
fun PostDailyRoute(
    paddingValues: PaddingValues,
    viewModel: PostDailyViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    onDiarySaved: (questId: String, diaryId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler { viewModel.cancelWriting() }
    LaunchedEffect(uiState.savedDiaryId) {
        uiState.savedDiaryId?.let { onDiarySaved(uiState.questId, it) }
    }
    LaunchedEffect(uiState.isCancelled) {
        if (uiState.isCancelled) navigateToBack()
    }
    PostDailyScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
        onNavigateToBack = viewModel::cancelWriting,
        onResultSelected = viewModel::selectResult,
        onMemoChange = viewModel::updateMemo,
        onSave = viewModel::saveDiary,
        onRetry = viewModel::loadQuest,
        onPhotoLoadError = viewModel::onPhotoLoadError,
    )
}

@Composable
fun PostDailyScreen(
    uiState: PostDailyUiState,
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit,
    onResultSelected: (DiaryResult) -> Unit,
    onMemoChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onPhotoLoadError: () -> Unit,
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography
    Column(modifier.fillMaxSize().background(colors.bgCanvas).imePadding()) {
        MjAppBar(type = MjAppBarType.Back, text = "일기 작성", onBackClick = onNavigateToBack)
        when {
            uiState.isLoading -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.actionPrimary)
            }
            uiState.loadError != null -> Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            ) {
                Text(uiState.loadError, style = typography.body2, color = colors.textSecondary)
                MjButton(text = "다시 시도", onClick = onRetry, enabled = !uiState.isBusy)
            }
            else -> Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                MjQuestSummary(
                    title = uiState.questTitle,
                    location = uiState.location,
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        .border(1.dp, colors.borderDefault, RoundedCornerShape(16.dp)),
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("퀘스트, 해봤나요?", style = typography.body2Strong, color = colors.textPrimary)
                    MjResultSelector(
                        selectedType = when (uiState.result) {
                            DiaryResult.DID -> MjResultSelectorType.DID
                            DiaryResult.MISSED -> MjResultSelectorType.MISSED
                            null -> MjResultSelectorType.NONE
                        },
                        onTypeSelected = { selected ->
                            if (!uiState.isBusy) when (selected) {
                                MjResultSelectorType.DID -> onResultSelected(DiaryResult.DID)
                                MjResultSelectorType.MISSED -> onResultSelected(DiaryResult.MISSED)
                                MjResultSelectorType.NONE -> Unit
                            }
                        },
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("경험 사진", style = typography.body2Strong, color = colors.textPrimary)
                        Text("필수", style = typography.caption, color = colors.textTertiary)
                    }
                    CapturedDiaryPhoto(uiState.photoUri, uiState.hasPhoto, onPhotoLoadError)
                    if (!uiState.hasPhoto) {
                        Text("사진이 없어요. 상세 화면으로 돌아가 다시 촬영해 주세요.",
                            color = colors.textError, style = typography.caption)
                    }
                }
                MjTextField(
                    value = uiState.memo,
                    onValueChange = { if (!uiState.isBusy) onMemoChange(it) },
                    label = "한 줄 메모 (선택 · 최대 50자)",
                    placeholder = "오늘의 경험을 짧게 남겨 보세요",
                    helperText = "${uiState.memoLength}/${PostDailyUiState.MAX_MEMO_LENGTH}",
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth().background(colors.bgSurface).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            uiState.saveError?.let { Text(it, color = colors.textError, style = typography.caption) }
            MjButton(
                text = when {
                    uiState.isSaving -> "저장 중…"
                    uiState.isCancelling -> "작성 취소 중…"
                    else -> "내 유학 일기에 남기기"
                },
                onClick = onSave,
                enabled = uiState.canSave,
            )
        }
    }
}

@Composable
private fun CapturedDiaryPhoto(photoUri: String, hasPhoto: Boolean, onLoadError: () -> Unit) {
    val colors = MjHackathonTheme.colors
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(16.dp)).background(colors.bgMuted),
        contentAlignment = Alignment.Center,
    ) {
        if (LocalInspectionMode.current || !hasPhoto) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(painterResource(R.drawable.ic_gallery), null, Modifier.size(28.dp), tint = colors.iconSecondary)
                Text(if (hasPhoto) "촬영된 사진" else "사진 없음", style = MjHackathonTheme.typography.caption,
                    color = colors.textSecondary)
            }
        } else {
            AsyncImage(
                model = photoUri,
                contentDescription = "퀘스트 경험 사진",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = { onLoadError() },
            )
        }
    }
}

class PostDailyPreviewStates : PreviewParameterProvider<PostDailyUiState> {
    private val sample = PostDailyUiState(
        questId = "mock-food", questTitle = "시장에서 만나는 첫 번째 한입", location = "영천시장",
        photoUri = "preview", hasPhoto = true, isLoading = false,
    )
    override val values = sequenceOf(
        sample,
        sample.copy(result = DiaryResult.DID, memo = "처음 먹어본 호떡, 생각보다 달았다."),
        sample.copy(result = DiaryResult.MISSED),
        sample.copy(result = DiaryResult.DID),
        sample.copy(result = DiaryResult.DID, isSaving = true),
        sample.copy(result = DiaryResult.DID, memo = "입력한 메모는 유지돼요", saveError = "저장하지 못했어요. 다시 시도해 주세요."),
        sample.copy(result = DiaryResult.DID, hasPhoto = false),
        sample.copy(isLoading = true),
        sample.copy(loadError = "퀘스트 정보를 불러오지 못했어요."),
        sample.copy(result = DiaryResult.DID, memo = "가".repeat(50)),
        sample.copy(isCancelling = true),
        sample.copy(result = DiaryResult.DID, savedDiaryId = "preview-saved"),
    )
}

@Preview(name = "일기 작성 - 상태별", widthDp = 360, heightDp = 850, showBackground = true)
@Composable
private fun PostDailyScreenPreview(@PreviewParameter(PostDailyPreviewStates::class) state: PostDailyUiState) {
    MjHackathonTheme {
        PostDailyScreen(state, onNavigateToBack = {}, onResultSelected = {}, onMemoChange = {},
            onSave = {}, onRetry = {}, onPhotoLoadError = {})
    }
}

@Preview(name = "일기 작성 - 선택·입력 실행 테스트", widthDp = 360, heightDp = 850, showBackground = true)
@Composable
private fun PostDailyInteractivePreview() {
    var state by remember { mutableStateOf(PostDailyPreviewStates().values.first()) }
    MjHackathonTheme {
        PostDailyScreen(
            uiState = state,
            onNavigateToBack = {},
            onResultSelected = { state = state.copy(result = it) },
            onMemoChange = {
                state = state.copy(memo = limitDiaryMemo(it))
            },
            onSave = { state = state.copy(saveError = "Preview에서는 실제 저장 없이 버튼 활성화만 확인합니다.") },
            onRetry = {},
            onPhotoLoadError = {},
        )
    }
}
