package com.cookandroid.mjhackathon.presentation.my

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.component.MjAppBar
import com.cookandroid.mjhackathon.core.designsystem.component.MjAppBarType
import com.cookandroid.mjhackathon.core.designsystem.component.MjDiaryCard
import com.cookandroid.mjhackathon.core.designsystem.component.MjDiaryCardState
import com.cookandroid.mjhackathon.core.designsystem.component.MjNavigationBar
import com.cookandroid.mjhackathon.core.designsystem.component.MjNavigationBarItem
import com.cookandroid.mjhackathon.core.designsystem.component.MjSmallButton
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult
import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary
import com.cookandroid.mjhackathon.data.model.my.DiaryCollection
import com.cookandroid.mjhackathon.data.model.my.DiaryMonthGroup

@Composable
fun MyRoute(
    paddingValues: PaddingValues,
    viewModel: MyViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    BackHandler(onBack = navigateToHome)
    MyScreen(
        uiState = uiState,
        modifier = Modifier.padding(paddingValues),
        onNavigateToHome = navigateToHome,
        onNavigateToProfile = navigateToProfile,
        onRetry = viewModel::refresh,
    )
}

@Composable
fun MyScreen(
    uiState: MyUiState,
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onRetry: () -> Unit,
) {
    val colors = MjHackathonTheme.colors
    Column(modifier.fillMaxSize().background(colors.bgCanvas)) {
        MjAppBar(type = MjAppBarType.Title, text = "나의 유학 일기")
        when {
            uiState.isLoading && uiState.totalCount == 0 -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = colors.actionPrimary) }
            uiState.errorMessage != null && uiState.totalCount == 0 -> Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            ) {
                Text(uiState.errorMessage, color = colors.textSecondary,
                    style = MjHackathonTheme.typography.body2, textAlign = TextAlign.Center)
                MjSmallButton(text = if (uiState.profileRequired) "프로필 설정하기" else "다시 시도",
                    onClick = if (uiState.profileRequired) onNavigateToProfile else onRetry)
            }
            uiState.totalCount == 0 -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) { MyEmptyContent(onNavigateToHome, Modifier.verticalScroll(rememberScrollState())) }
            else -> LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (uiState.isLoading) {
                    item(key = "refresh") { LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = colors.actionPrimary) }
                }
                if (uiState.errorMessage != null) {
                    item(key = "error") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(uiState.errorMessage, color = colors.textError, style = MjHackathonTheme.typography.caption)
                            MjSmallButton(text = if (uiState.profileRequired) "프로필 설정하기" else "다시 시도",
                    onClick = if (uiState.profileRequired) onNavigateToProfile else onRetry)
                        }
                    }
                }
                item(key = "stats") { MyStatistics(uiState) }
                uiState.months.forEach { month ->
                    item(key = "month:${month.key}") {
                        Text(month.title, modifier = Modifier.padding(top = 8.dp),
                            color = colors.textSecondary, style = MjHackathonTheme.typography.body2)
                    }
                    items(month.entries, key = { "diary:${it.diary.id}" }) { entry ->
                        val diary = entry.diary
                        MjDiaryCard(
                            state = when (diary.result) {
                                DiaryResult.DID -> MjDiaryCardState.Did
                                DiaryResult.MISSED -> MjDiaryCardState.Missed
                            },
                            date = entry.displayDate,
                            location = diary.location,
                            title = diary.questTitle,
                            memo = diary.memo,
                            imageUri = diary.photoUri,
                        )
                    }
                }
            }
        }
        MjNavigationBar(
            selectedItem = MjNavigationBarItem.Diary,
            onItemClick = { item ->
                when (item) {
                    MjNavigationBarItem.Explore -> onNavigateToHome()
                    MjNavigationBarItem.Diary -> Unit
                    MjNavigationBarItem.Profile -> onNavigateToProfile()
                }
            },
        )
    }
}

@Composable
private fun MyStatistics(uiState: MyUiState) {
    val colors = MjHackathonTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
            .background(colors.bgSurface, RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(uiState.totalCount to "전체 기록", uiState.didCount to "해봤어요!", uiState.missedCount to "이번엔 못했어요")
            .forEach { (count, label) ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(count.toString(), color = colors.textPrimary, style = MjHackathonTheme.typography.title3)
                    Text(label, color = colors.textSecondary, style = MjHackathonTheme.typography.caption,
                        textAlign = TextAlign.Center)
                }
            }
    }
}

@Composable
private fun MyEmptyContent(onNavigateToHome: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MjHackathonTheme.colors
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(72.dp).background(colors.bgAccentSubtle, CircleShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_book), null, Modifier.size(28.dp), tint = colors.textAccent)
        }
        Text("아직 기록이 없어요", color = colors.textPrimary, style = MjHackathonTheme.typography.body1Strong)
        Text("첫 퀘스트를 경험하고 사진으로 남겨 보세요", color = colors.textSecondary,
            style = MjHackathonTheme.typography.body2, textAlign = TextAlign.Center)
        MjSmallButton(text = "동네 퀘스트 보기", onClick = onNavigateToHome)
    }
}

class MyPreviewStates : PreviewParameterProvider<MyUiState> {
    private val samples = listOf(
        SavedDiary("preview-1", "mock-food", "시장에서 만나는 첫 번째 한입", "영천시장", DiaryResult.MISSED,
            "", "오늘은 시장이 문을 닫아 다음에 다시 오기로 했다.", "2026-11-05T12:00:00+09:00"),
        SavedDiary("preview-2", "mock-tour", "독립문 앞, 서울의 한 장면", "독립문", DiaryResult.DID,
            "", "역사책에서 본 장소를 직접 걸어봤다.", "2026-11-02T12:00:00+09:00"),
    )
    private val filled = previewDiaryState(samples)
    override val values = sequenceOf(
        filled,
        previewDiaryState(emptyList()),
        MyUiState(),
        MyUiState(isLoading = false, errorMessage = "일기를 불러오지 못했어요. 다시 시도해 주세요."),
        MyUiState(isLoading = false, errorMessage = "프로필을 먼저 저장해 주세요.", profileRequired = true),
        filled.copy(isLoading = true),
        filled.copy(errorMessage = "새 기록을 불러오지 못했어요. 다시 시도해 주세요."),
        previewDiaryState(samples.map { it.copy(result = DiaryResult.DID, memo = "") }),
        previewDiaryState(samples.map { it.copy(result = DiaryResult.MISSED) }),
        previewDiaryState(samples + samples.first().copy(id = "preview-3", savedAt = "2026-10-31T12:00:00+09:00")),
    )
}

@Preview(name = "나의 유학 일기 - 목록·빈 상태·로딩·오류", widthDp = 360, heightDp = 900, showBackground = true)
@Composable
private fun MyScreenPreview(@PreviewParameter(MyPreviewStates::class) state: MyUiState) {
    MjHackathonTheme {
        MyScreen(state, onNavigateToHome = {}, onNavigateToProfile = {}, onRetry = {})
    }
}

/** Preview에서만 사용하는 서버 응답 형태의 고정 샘플. */
private fun previewDiaryState(diaries: List<SavedDiary>): MyUiState = MyUiState.fromCollection(
    DiaryCollection(
        totalCount = diaries.size,
        experiencedCount = diaries.count { it.result == DiaryResult.DID },
        notExperiencedCount = diaries.count { it.result == DiaryResult.MISSED },
        groups = diaries.groupBy { it.savedAt.take(7) }.map { (month, entries) ->
            DiaryMonthGroup(month, entries)
        },
    ),
)
