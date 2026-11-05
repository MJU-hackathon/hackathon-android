package com.cookandroid.mjhackathon.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cookandroid.mjhackathon.core.designsystem.component.MjAppBar
import com.cookandroid.mjhackathon.core.designsystem.component.MjAppBarType
import com.cookandroid.mjhackathon.core.designsystem.component.MjNavigationBar
import com.cookandroid.mjhackathon.core.designsystem.component.MjNavigationBarItem
import com.cookandroid.mjhackathon.core.designsystem.component.MjQuestCard
import com.cookandroid.mjhackathon.core.designsystem.component.MjSmallButton
import com.cookandroid.mjhackathon.core.designsystem.component.MjStayCard
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.presentation.home.component.MjQuestEmptyState

@Composable
fun HomeRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToDetail: (String) -> Unit,
    navigateToDiary: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadQuests()
            while (true) {
                val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                val midnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
                delay(Duration.between(now, midnight).toMillis().coerceAtLeast(1L))
                viewModel.loadQuests()
            }
        }
    }
    HomeScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
        onNavigateToDetail = navigateToDetail,
        onRetry = viewModel::loadQuests,
        onNavigateToDiary = navigateToDiary,
        onNavigateToProfile = navigateToProfile,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToDiary: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onRetry: () -> Unit = {},
) {
    val colors = MjHackathonTheme.colors
    Column(modifier = modifier.fillMaxSize().background(colors.bgCanvas)) {
        MjAppBar(type = MjAppBarType.Logo, text = "K-Log")
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "stay") { MjStayCard(data = uiState.stay) }
            item(key = "heading") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "오늘의 동네 퀘스트",
                        modifier = Modifier.weight(1f),
                        style = MjHackathonTheme.typography.body1Strong,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = if (uiState.isLoading || uiState.errorMessage != null) ""
                            else "오늘 남은 퀘스트 ${uiState.remainingQuestCount}/${HomeUiState.DAILY_QUEST_COUNT}",
                        style = MjHackathonTheme.typography.caption,
                        color = colors.textTertiary,
                    )
                }
            }
            if (uiState.isLoading) {
                item(key = "loading") {
                    Text("오늘의 퀘스트를 불러오고 있어요…", color = colors.textSecondary)
                }
            }
            uiState.errorMessage?.let { message ->
                item(key = "error") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(message, color = colors.textError)
                        MjSmallButton(
                            text = if (uiState.profileRequired) "프로필 설정하기" else "다시 시도",
                            onClick = if (uiState.profileRequired) onNavigateToProfile else onRetry,
                        )
                    }
                }
            }
            if (uiState.quests.isEmpty() && !uiState.isLoading && uiState.errorMessage == null) {
                item(key = "empty") { MjQuestEmptyState() }
            } else {
                items(uiState.quests, key = { it.id }) { quest ->
                    MjQuestCard(
                        title = quest.title,
                        category = quest.category,
                        location = quest.location,
                        imageUrl = quest.imageUrl,
                        estimatedMinutes = quest.estimatedMinutes,
                        reason = quest.reason,
                        onClick = { onNavigateToDetail(quest.id) },
                    )
                }
            }
        }
        MjNavigationBar(
            selectedItem = MjNavigationBarItem.Explore,
            onItemClick = { item ->
                when (item) {
                    MjNavigationBarItem.Explore -> Unit
                    MjNavigationBarItem.Diary -> onNavigateToDiary()
                    MjNavigationBarItem.Profile -> onNavigateToProfile()
                }
            },
        )
    }
}

class HomePreviewStates : PreviewParameterProvider<HomeUiState> {
    override val values = sequenceOf(
        HomeUiState(),
        HomeUiState(isLoading = false, errorMessage = "서버에 연결할 수 없어요."),
        HomeUiState(isLoading = false, errorMessage = "프로필을 먼저 저장해 주세요.", profileRequired = true),
        HomeUiState.mock().copy(isLoading = true),
        HomeUiState.mock().copy(errorMessage = "퀘스트를 새로 불러오지 못했어요."),
    ) + (3 downTo 0).asSequence().map { count ->
        HomeUiState.mock().let { it.copy(quests = it.quests.take(count)) }
    }
}

@Preview(name = "홈 - 로딩·오류·남은 퀘스트", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun HomeScreenPreview(@PreviewParameter(HomePreviewStates::class) uiState: HomeUiState) {
    MjHackathonTheme {
        HomeScreen(uiState, onNavigateToDetail = {}, onNavigateToDiary = {}, onNavigateToProfile = {})
    }
}

/** 실행용 Preview: 위 버튼으로 처리/초기화하며 두 화면 사이 전환을 확인한다. */
@Preview(name = "홈 - 퀘스트 제거 실행 테스트", widthDp = 360, heightDp = 850, showBackground = true)
@Composable
private fun HomeScreenInteractivePreview() {
    var state by remember { mutableStateOf(HomeUiState.mock()) }
    MjHackathonTheme {
        Column(Modifier.fillMaxSize().background(MjHackathonTheme.colors.bgCanvas)) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MjSmallButton(text = "1개 처리", onClick = { state = state.copy(quests = state.quests.drop(1)) })
                MjSmallButton(text = "초기화", onClick = { state = HomeUiState.mock() })
            }
            HomeScreen(
                uiState = state,
                modifier = Modifier.weight(1f),
                onNavigateToDetail = {},
                onNavigateToDiary = {},
                onNavigateToProfile = {},
            )
        }
    }
}
