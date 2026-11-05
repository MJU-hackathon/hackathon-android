package com.cookandroid.mjhackathon.presentation.detail

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
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
import com.cookandroid.mjhackathon.core.designsystem.component.MjMapCard
import com.cookandroid.mjhackathon.core.designsystem.component.MjStepItem
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.core.util.noRippleClickable
import com.cookandroid.mjhackathon.data.model.detail.QuestDetail

@Composable
fun DetailRoute(
    paddingValues: PaddingValues,
    viewModel: DetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    onQuestPassed: (String) -> Unit,
    navigateToPostDaily: (String, String, String, String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var cameraError by rememberSaveable { mutableStateOf<String?>(null) }
    val camera = rememberDetailPhotoCapture(
        questId = viewModel.questId,
        onPhotoCaptured = navigateToPostDaily,
        onError = { cameraError = it },
    )
    val context = LocalContext.current
    LaunchedEffect(uiState.passedQuestId) {
        uiState.passedQuestId?.let(onQuestPassed)
    }
    DetailScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
        isCameraBusy = camera.isBusy,
        cameraError = cameraError,
        onNavigateToBack = navigateToBack,
        onRetry = viewModel::loadQuest,
        onPass = viewModel::passQuest,
        onRecordPhoto = {
            if (viewModel.uiState.value.canAct) { cameraError = null; camera.launch() }
        },
        onOpenMap = { quest ->
            if (!quest.hasMapCoordinates) return@DetailScreen
            val geoUri = Uri.parse("geo:${quest.latitude},${quest.longitude}?q=" +
                    Uri.encode("${quest.latitude},${quest.longitude}(${quest.placeName})"))
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
            } catch (_: android.content.ActivityNotFoundException) {
                // 지도 앱이 없어도 내장 지도는 그대로 표시한다.
            }
        },
    )
}

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    modifier: Modifier = Modifier,
    isCameraBusy: Boolean = false,
    cameraError: String? = null,
    onNavigateToBack: () -> Unit,
    onRetry: () -> Unit,
    onPass: () -> Unit,
    onRecordPhoto: () -> Unit,
    onOpenMap: (QuestDetail) -> Unit,
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography
    Column(modifier.fillMaxSize().background(colors.bgCanvas)) {
        MjAppBar(type = MjAppBarType.Back, text = "동네 탐험", onBackClick = onNavigateToBack)
        when {
            uiState.isLoading -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colors.actionPrimary)
            }
            uiState.quest == null -> Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            ) {
                Text(uiState.loadError ?: "퀘스트를 찾을 수 없어요.", color = colors.textSecondary, style = typography.body2)
                MjButton(text = "다시 시도", onClick = onRetry)
            }
            else -> {
                val quest = uiState.quest
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    DetailHeroImage(quest.imageUrl)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (quest.estimatedMinutes > 0) "${quest.category} · ${quest.estimatedMinutes}분" else quest.category,
                            modifier = Modifier.background(colors.bgAccentSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            color = colors.textAccent,
                            style = typography.captionStrong,
                        )
                        Text(quest.title, color = colors.textPrimary, style = typography.title2)
                        if (quest.reason.isNotBlank()) {
                            Text(quest.reason, color = colors.textBrand, style = typography.body2)
                        }
                        Text(quest.description, color = colors.textSecondary, style = typography.body2)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("오늘의 작은 도전", color = colors.textPrimary, style = typography.body1Strong)
                        Column(
                            modifier = Modifier.fillMaxWidth().background(colors.bgSurface, RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            quest.steps.forEachIndexed { index, text -> MjStepItem(step = index + 1, text = text) }
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("장소", color = colors.textPrimary, style = typography.body1Strong)
                        val latitude = quest.latitude
                        val longitude = quest.longitude
                        if (quest.hasMapCoordinates && latitude != null && longitude != null) {
                            MjMapCard(
                                latitude = latitude, longitude = longitude,
                                placeName = quest.placeName, address = quest.address.orEmpty(),
                                onClick = { onOpenMap(quest) },
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(quest.placeName, color = colors.textPrimary, style = typography.body2)
                                quest.address?.takeIf { it.isNotBlank() }?.let {
                                    Text(it, color = colors.textSecondary, style = typography.caption)
                                }
                                Text("이 장소의 지도 위치 정보가 없어요.", color = colors.textSecondary, style = typography.caption)
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth().background(colors.bgSurface).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    (uiState.passError ?: cameraError)?.let {
                        Text(it, color = colors.textError, style = typography.caption)
                    }
                    if (quest.status != "AVAILABLE") {
                        Text("이미 처리됐거나 진행할 수 없는 퀘스트예요.", color = colors.textSecondary, style = typography.caption)
                    }
                    val enabled = uiState.canAct && !isCameraBusy
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.width(112.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (enabled) colors.actionSecondary else colors.actionDisabled)
                                .border(1.dp, colors.borderDefault, RoundedCornerShape(16.dp))
                                .semantics { role = Role.Button; if (!enabled) disabled() }
                                .then(if (enabled) Modifier.noRippleClickable(onPass) else Modifier)
                                .padding(horizontal = 12.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                if (uiState.isPassing) "처리 중…" else "이번엔 패스",
                                color = if (enabled) colors.textPrimary else colors.textDisabled,
                                style = typography.buttonL,
                                textAlign = TextAlign.Center,
                            )
                        }
                        MjButton(
                            text = if (isCameraBusy) "카메라 준비 중…" else "사진으로 기록하기",
                            onClick = onRecordPhoto,
                            modifier = Modifier.weight(1f),
                            enabled = enabled,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHeroImage(imageUrl: String?) {
    val colors = MjHackathonTheme.colors
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(20.dp))
            .background(colors.bgMuted),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isNullOrBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(painterResource(R.drawable.ic_gallery), null, Modifier.size(28.dp), tint = colors.iconSecondary)
                Text("퀘스트 대표 이미지 (16:10)", color = colors.textSecondary, style = MjHackathonTheme.typography.caption)
            }
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "퀘스트 대표 이미지",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_gallery),
                error = painterResource(R.drawable.ic_gallery),
            )
        }
    }
}

class DetailPreviewStates : PreviewParameterProvider<DetailUiState> {
    private val sample = QuestDetail(
        id = "mock-food", category = "음식", title = "시장에서 만나는 첫 번째 한입",
        description = "서대문 영천시장에서 한국 시장 음식을 직접 골라 주문해 보는 퀘스트예요.",
        steps = listOf("시장에서 먹어보고 싶은 음식 찾기", "한국어로 주문해 보기", "음식이나 시장 풍경 사진 찍기"),
        placeName = "영천시장", address = "서대문구 영천동", latitude = 37.5704, longitude = 126.9624,
        estimatedMinutes = 30, reason = "한국 음식에 관심이 있는 당신에게 추천해요.",
    )
    override val values = sequenceOf(
        DetailUiState(isLoading = false, quest = sample),
        DetailUiState(isLoading = false, quest = sample.copy(address = null, latitude = null, longitude = null)),
        DetailUiState(isLoading = false, quest = sample.copy(status = "SKIPPED")),
        DetailUiState(isLoading = false, quest = sample, actionsUnavailable = true,
            passError = "이미 처리했거나 날짜가 지난 퀘스트예요."),
        DetailUiState(),
        DetailUiState(isLoading = false, loadError = "퀘스트를 불러오지 못했어요."),
        DetailUiState(isLoading = false, quest = sample, isPassing = true),
        DetailUiState(isLoading = false, quest = sample, passError = "패스하지 못했어요. 다시 시도해 주세요."),
        DetailUiState(isLoading = false, quest = sample, passedQuestId = sample.id),
    )
}

@Preview(name = "상세 - 카메라 준비 중", widthDp = 360, heightDp = 1050, showBackground = true)
@Composable
private fun DetailCameraBusyPreview() {
    MjHackathonTheme {
        DetailScreen(
            uiState = DetailPreviewStates().values.first(),
            isCameraBusy = true,
            onNavigateToBack = {}, onRetry = {}, onPass = {}, onRecordPhoto = {}, onOpenMap = {},
        )
    }
}

@Preview(name = "상세 - 카메라 오류", widthDp = 360, heightDp = 1050, showBackground = true)
@Composable
private fun DetailCameraErrorPreview() {
    MjHackathonTheme {
        DetailScreen(
            uiState = DetailPreviewStates().values.first(),
            cameraError = "카메라를 열지 못했어요. 다시 시도해 주세요.",
            onNavigateToBack = {}, onRetry = {}, onPass = {}, onRecordPhoto = {}, onOpenMap = {},
        )
    }
}

@Preview(name = "상세 - 성공·로딩·오류·패스 중·패스 실패", widthDp = 360, heightDp = 1050, showBackground = true)
@Composable
private fun DetailScreenPreview(@PreviewParameter(DetailPreviewStates::class) state: DetailUiState) {
    MjHackathonTheme {
        DetailScreen(state, onNavigateToBack = {}, onRetry = {}, onPass = {}, onRecordPhoto = {}, onOpenMap = {})
    }
}
