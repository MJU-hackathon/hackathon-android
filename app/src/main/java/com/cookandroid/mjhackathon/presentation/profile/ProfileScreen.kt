package com.cookandroid.mjhackathon.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cookandroid.mjhackathon.core.designsystem.component.MjAppBar
import com.cookandroid.mjhackathon.core.designsystem.component.MjAppBarType
import com.cookandroid.mjhackathon.core.designsystem.component.MjButton
import com.cookandroid.mjhackathon.core.designsystem.component.MjChip
import com.cookandroid.mjhackathon.core.designsystem.component.MjTextField
import com.cookandroid.mjhackathon.core.designsystem.component.MjTextFieldType
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import java.time.LocalDate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.cookandroid.mjhackathon.core.designsystem.component.MjBirthDatePicker
import com.cookandroid.mjhackathon.core.designsystem.component.MjStayPeriodPicker
import com.cookandroid.mjhackathon.core.designsystem.component.MjStayDateField

@Composable
fun ProfileRoute(
    paddingValues: PaddingValues,
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateToBack: () -> Unit = {},
    onSaveProfile: (ProfileState) -> Unit = {},
    showTopBar: Boolean = true,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onSaved by rememberUpdatedState(onSaveProfile)
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.consumeSaveSuccess()
            onSaved(uiState)
        }
    }

    ProfileScreen(
        uiState = uiState,
        showTopBar = showTopBar,
        onStateChange = viewModel::updateState,
        onNavigateToBack = navigateToBack,
        onSaveClick = viewModel::saveProfile,
        // Scaffold에서 적용한 시스템 여백은 imePadding에서 다시 계산하지 않는다.
        modifier = Modifier
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues),
    )
}

/** 입력값과 메뉴·날짜 선택 상태는 호출부에서 관리한다. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    uiState: ProfileState,
    modifier: Modifier = Modifier,
    onStateChange: (ProfileState) -> Unit = {},
    onNavigateToBack: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    languages: List<String> = profileLocales.keys.toList(),
    interestOptions: List<String> = profileInterests.keys.toList(),
    showTopBar: Boolean = true,
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgCanvas)
            .imePadding(),
    ) {
        if (showTopBar) {
            MjAppBar(type = MjAppBarType.Back, text = "정보", onBackClick = onNavigateToBack)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(
                modifier = Modifier.padding(bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("프로필을 알려 주세요", color = colors.textPrimary, style = typography.title1)
                Text(
                    "입력한 정보로 내 주변 퀘스트를 추천해요",
                    color = colors.textSecondary,
                    style = typography.body1
                )
            }

            MjTextField(
                value = uiState.nickname,
                onValueChange = { onStateChange(uiState.copy(nickname = it)) },
                label = "닉네임",
                placeholder = "사용할 이름",
                helperText = if (uiState.nicknameError) "닉네임은 2~12자로 입력해 주세요" else "2~12자",
                isError = uiState.nicknameError,
            )

            MjTextField(
                value = uiState.language,
                onValueChange = { language ->
                    onStateChange(uiState.copy(language = language, languageMenuExpanded = false))
                },
                label = "사용 언어",
                helperText = "사용할 언어를 선택해 주세요",
                type = MjTextFieldType.Dropdown,
                dropdownOptions = languages,
                dropdownExpanded = uiState.languageMenuExpanded,
                onDropdownExpandedChange = { expanded ->
                    onStateChange(uiState.copy(languageMenuExpanded = expanded))
                },
            )

            MjTextField(
                value = uiState.birthday,
                onValueChange = {},
                label = "생년월일",
                placeholder = "YYYY-MM-DD",
                type = MjTextFieldType.Date,
                onClick = { onStateChange(uiState.copy(activeDateField = ProfileDateField.BIRTHDAY)) },
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("한국 체류 기간", color = colors.textPrimary, style = typography.body2Strong)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MjTextField(
                        value = uiState.stayStart,
                        onValueChange = { onStateChange(uiState.copy(stayStart = it)) },
                        label = "시작일",
                        placeholder = "YYYY-MM-DD",
                        type = MjTextFieldType.Date,
                        onClick = { onStateChange(uiState.copy(activeDateField = ProfileDateField.STAY_START)) },
                        modifier = Modifier.weight(1f),
                    )
                    MjTextField(
                        value = uiState.stayEnd,
                        onValueChange = { onStateChange(uiState.copy(stayEnd = it)) },
                        label = "종료일 (귀국 예정일)",
                        placeholder = "YYYY-MM-DD",
                        isError = uiState.stayDateError,
                        type = MjTextFieldType.Date,
                        onClick = { onStateChange(uiState.copy(activeDateField = ProfileDateField.STAY_END)) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (uiState.stayDateError) {
                    Text(
                        "종료일은 시작일 이후로 선택해 주세요",
                        color = colors.textError,
                        style = typography.caption
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("관심사", color = colors.textPrimary, style = typography.body2Strong)
                    Text("1개 이상 · 복수 선택", color = colors.textTertiary, style = typography.caption)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    interestOptions.forEach { interest ->
                        MjChip(
                            text = interest,
                            selected = interest in uiState.interests,
                            onClick = {
                                val interests = if (interest in uiState.interests) {
                                    uiState.interests - interest
                                } else {
                                    uiState.interests + interest
                                }
                                onStateChange(uiState.copy(interests = interests))
                            },
                        )
                    }
                }
            }
        }

        Box(Modifier
            .fillMaxWidth()
            .background(colors.bgSurface)
            .padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.saveErrorMessage?.let { message ->
                    Text(message, color = colors.textError, style = typography.caption)
                }
                MjButton(
                    text = if (uiState.isSaving) "저장 중…" else "정보 저장하기",
                    onClick = onSaveClick,
                    enabled = uiState.canSave && !uiState.isSaving && !uiState.isSaved,
                )
            }
        }
    }

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(uiState.activeDateField) {
        if (uiState.activeDateField != null) {
            focusManager.clearFocus()
            keyboard?.hide()
        }
    }
    val dismissPicker = { onStateChange(uiState.copy(activeDateField = null)) }
    when (uiState.activeDateField) {
        ProfileDateField.BIRTHDAY -> MjBirthDatePicker(
            initialDate = uiState.birthday.toProfileDate(),
            onDismiss = dismissPicker,
            onConfirm = { onStateChange(uiState.copy(birthday = it.toString(), activeDateField = null)) },
        )
        ProfileDateField.STAY_START, ProfileDateField.STAY_END -> MjStayPeriodPicker(
            initialStart = uiState.stayStart.toProfileDate(),
            initialEnd = uiState.stayEnd.toProfileDate(),
            initialField = if (uiState.activeDateField == ProfileDateField.STAY_END)
                MjStayDateField.End else MjStayDateField.Start,
            onDismiss = dismissPicker,
            onConfirm = { start, end ->
                onStateChange(uiState.copy(stayStart = start.toString(), stayEnd = end.toString(), activeDateField = null))
            },
        )
        null -> Unit
    }
}

private fun String.toProfileDate(): LocalDate? = runCatching { LocalDate.parse(this) }.getOrNull()

class ProfilePreviewProvider : PreviewParameterProvider<ProfileState> {
    private val filled = ProfileState(
        nickname = "태훈", birthday = "2000-01-01", stayStart = "2026-09-01", stayEnd = "2027-02-28",
        interests = setOf("KPOP", "영화", "음식"),
    )
    override val values = sequenceOf(
        ProfileState(),
        filled,
        filled.copy(isSaving = true),
        filled.copy(saveErrorMessage = "정보를 저장하지 못했어요. 잠시 후 다시 시도해 주세요."),
        filled.copy(nickname = "안"),
        filled.copy(stayEnd = "2026-08-01"),
        filled.copy(interests = emptySet()),
        filled.copy(languageMenuExpanded = true),
        filled.copy(activeDateField = ProfileDateField.BIRTHDAY),
        filled.copy(activeDateField = ProfileDateField.STAY_START),
        filled.copy(activeDateField = ProfileDateField.STAY_END),
    )
}

@Preview(name = "프로필 - 입력 상태", widthDp = 390, heightDp = 1000, showBackground = true)
@Composable
private fun ProfileScreenPreview(@PreviewParameter(ProfilePreviewProvider::class) state: ProfileState) {
    MjHackathonTheme {
        ProfileScreen(uiState = state)
    }
}

@Preview(name = "프로필 - 최초 진입", widthDp = 390, heightDp = 1000, showBackground = true)
@Composable
private fun InitialProfileScreenPreview(@PreviewParameter(ProfilePreviewProvider::class) state: ProfileState) {
    MjHackathonTheme {
        ProfileScreen(uiState = state, showTopBar = false)
    }
}

// 키보드로 가용 높이가 줄어든 레이아웃 확인용. 실제 IME inset은 기기에서 확인한다.
@Preview(name = "프로필 - 입력 중 작은 높이", widthDp = 390, heightDp = 480, showBackground = true)
@Composable
private fun CompactProfileScreenPreview() {
    MjHackathonTheme {
        ProfileScreen(uiState = ProfileState(nickname = "태훈"))
    }
}
