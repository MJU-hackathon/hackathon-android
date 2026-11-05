package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.core.util.noRippleClickable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

enum class MjTextFieldType {
    Text,
    Dropdown,
    Date,
}

/**
 * Dropdown은 같은 테두리 안에서 아래로 펼쳐지는 아코디언 목록을 제공한다.
 * 기본 목록은 언어 목업이며 선택은 onValueChange로 전달한다. Date는 onClick에서 달력을 연다.
 * 펼침 상태는 기본적으로 내부 관리한다. 외부 제어 시 dropdownExpanded와
 * onDropdownExpandedChange를 함께 전달한다. 기존 DropdownMenu를 별도로 연결할 필요가 없다.
 */
@Composable
fun MjTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "입력해 주세요",
    helperText: String = "",
    isError: Boolean = false,
    type: MjTextFieldType = MjTextFieldType.Text,
    onClick: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    dropdownOptions: List<String> = listOf("한국어", "영어", "베트남어", "중국어"),
    dropdownExpanded: Boolean? = null,
    onDropdownExpandedChange: (Boolean) -> Unit = {},
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography
    val shape = RoundedCornerShape(12.dp)
    var internalExpanded by rememberSaveable(type) { mutableStateOf(false) }
    val expanded = dropdownExpanded ?: internalExpanded
    val changeExpanded: (Boolean) -> Unit = { newValue ->
        if (dropdownExpanded == null) internalExpanded = newValue
        onDropdownExpandedChange(newValue)
    }
    val fieldModifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 56.dp)
        .clip(shape)
        .background(colors.bgSurface)
        .border(
            width = if (isError) 2.dp else 1.dp,
            color = if (isError) colors.borderError else colors.borderDefault,
            shape = shape,
        )
        .semantics {
            contentDescription = label
            if (isError) error(helperText.ifEmpty { "입력값을 확인해 주세요" })
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            color = colors.textPrimary,
            style = typography.body2Strong,
        )

        if (type == MjTextFieldType.Text) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = fieldModifier.padding(horizontal = 16.dp, vertical = 16.dp),
                textStyle = typography.body1.copy(color = colors.textPrimary),
                singleLine = true,
                keyboardOptions = keyboardOptions,
                cursorBrush = SolidColor(colors.textBrand),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = colors.textTertiary,
                                style = typography.body1,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        } else if (type == MjTextFieldType.Dropdown) {
            MjTextFieldAccordion(
                value = value,
                placeholder = placeholder,
                options = dropdownOptions.distinct(),
                expanded = expanded,
                modifier = fieldModifier,
                onToggle = { changeExpanded(!expanded) },
                onSelect = { option ->
                    changeExpanded(false)
                    onValueChange(option)
                },
            )
        } else {
            Row(
                modifier = fieldModifier
                    .semantics(mergeDescendants = true) { role = Role.Button }
                    .noRippleClickable(onClick)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = value.ifEmpty { placeholder },
                    modifier = Modifier.weight(1f),
                    color = if (value.isEmpty()) colors.textTertiary else colors.textPrimary,
                    style = typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_calender),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = colors.iconSecondary,
                )
            }
        }

        if (helperText.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isError) {
                    Icon(
                        painter = painterResource(R.drawable.ic_alert),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.textError,
                    )
                }
                Text(
                    text = helperText,
                    color = if (isError) colors.textError else colors.textSecondary,
                    style = typography.caption,
                )
            }
        }
    }
}

@Composable
private fun MjTextFieldAccordion(
    value: String,
    placeholder: String,
    options: List<String>,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(200),
        label = "TextField dropdown arrow",
    )
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    stateDescription = if (expanded) "목록 펼쳐짐" else "목록 접힘"
                }
                .noRippleClickable(onToggle)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = value.ifEmpty { placeholder },
                modifier = Modifier.weight(1f),
                color = if (value.isEmpty()) colors.textTertiary else colors.textPrimary,
                style = typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                painter = painterResource(R.drawable.ic_down_arrow),
                contentDescription = null,
                modifier = Modifier.size(24.dp).rotate(arrowRotation),
                tint = colors.iconSecondary,
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(200), expandFrom = Alignment.Top) + fadeIn(tween(150)),
            exit = shrinkVertically(tween(200), shrinkTowards = Alignment.Top) + fadeOut(tween(150)),
        ) {
            Column {
                HorizontalDivider(color = colors.borderDefault)
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    if (options.isEmpty()) {
                        Text("선택할 항목이 없어요", modifier = Modifier.padding(16.dp),
                            style = typography.body2, color = colors.textTertiary)
                    }
                    options.forEach { option ->
                        val isSelected = option == value
                        Row(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                .background(if (isSelected) colors.bgBrandSubtle else colors.bgSurface)
                                .semantics(mergeDescendants = true) {
                                    role = Role.RadioButton
                                    selected = isSelected
                                }
                                .noRippleClickable { onSelect(option) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.weight(1f),
                                color = if (isSelected) colors.textBrand else colors.textPrimary,
                                style = if (isSelected) typography.body1Strong else typography.body1,
                            )
                            if (isSelected) {
                                Icon(painterResource(R.drawable.ic_check), null, Modifier.size(20.dp), tint = colors.iconBrand)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MjTextFieldPreviewState(
    val type: MjTextFieldType,
    val value: String,
    val isError: Boolean,
    val isExpanded: Boolean = false,
) {
    override fun toString(): String = "$type - ${if (isExpanded) "펼침 · " else ""}${if (isError) "오류" else if (value.isEmpty()) "빈 값" else "입력됨"}"
}

class MjTextFieldPreviewProvider : PreviewParameterProvider<MjTextFieldPreviewState> {
    override val values = MjTextFieldType.entries.asSequence().flatMap { type ->
        sequenceOf(
            MjTextFieldPreviewState(type, "", false),
            MjTextFieldPreviewState(type, "입력해 주세요", false),
            MjTextFieldPreviewState(type, "입력해 주세요", true),
        )
    } + sequenceOf(
        MjTextFieldPreviewState(MjTextFieldType.Dropdown, "", false, true),
        MjTextFieldPreviewState(MjTextFieldType.Dropdown, "한국어", false, true),
        MjTextFieldPreviewState(MjTextFieldType.Dropdown, "한국어", true, true),
    )
}

@Preview(name = "MjTextField", widthDp = 360, showBackground = true)
@Composable
private fun MjTextFieldPreview(
    @PreviewParameter(MjTextFieldPreviewProvider::class) state: MjTextFieldPreviewState,
) {
    var value by remember(state) { mutableStateOf(state.value) }
    var expanded by remember(state) { mutableStateOf(state.isExpanded) }
    MjHackathonTheme {
        MjTextField(
            value = value,
            onValueChange = { value = it },
            label = "라벨",
            helperText = "도움말",
            isError = state.isError,
            type = state.type,
            dropdownExpanded = expanded,
            onDropdownExpandedChange = { expanded = it },
            modifier = Modifier
                .background(MjHackathonTheme.colors.bgCanvas)
                .padding(16.dp),
        )
    }
}

@Preview(name = "MjTextField - 드롭다운 목록 없음", widthDp = 360, showBackground = true)
@Composable
private fun MjTextFieldEmptyDropdownPreview() {
    var expanded by remember { mutableStateOf(true) }
    MjHackathonTheme {
        MjTextField(
            value = "",
            onValueChange = {},
            label = "사용 언어",
            type = MjTextFieldType.Dropdown,
            dropdownOptions = emptyList(),
            dropdownExpanded = expanded,
            onDropdownExpandedChange = { expanded = it },
            modifier = Modifier.background(MjHackathonTheme.colors.bgCanvas).padding(16.dp),
        )
    }
}

/** Preview의 Run 버튼으로 일반 입력, 언어 선택, 날짜 선택을 한 화면에서 테스트한다. */
@Preview(
    name = "MjTextField - 전체 타입 실행 테스트",
    widthDp = 360,
    heightDp = 760,
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun MjTextFieldAllTypesPreview() {
    var showError by rememberSaveable { mutableStateOf(false) }

    MjHackathonTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MjHackathonTheme.colors.bgCanvas)
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = "텍스트필드 테스트",
                color = MjHackathonTheme.colors.textPrimary,
                style = MjHackathonTheme.typography.title2,
            )
            Text(
                text = if (showError) "오류 표시 끄기" else "오류 표시 켜기",
                color = MjHackathonTheme.colors.textBrand,
                style = MjHackathonTheme.typography.buttonM,
                modifier = Modifier
                    .semantics { role = Role.Button }
                    .noRippleClickable { showError = !showError }
                    .padding(vertical = 12.dp),
            )

            MjTextFieldType.entries.forEach { type ->
                MjTextFieldInteractiveSample(type = type, isError = showError)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MjTextFieldInteractiveSample(
    type: MjTextFieldType,
    isError: Boolean,
) {
    var value by rememberSaveable(type.name) {
        mutableStateOf(if (type == MjTextFieldType.Dropdown) "한국어" else "")
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val colors = MjHackathonTheme.colors
    val label = when (type) {
        MjTextFieldType.Text -> "이름"
        MjTextFieldType.Dropdown -> "사용 언어"
        MjTextFieldType.Date -> "생년월일"
    }

    MjTextField(
        value = value,
        onValueChange = { value = it },
        label = label,
        type = type,
        placeholder = if (type == MjTextFieldType.Date) "YYYY-MM-DD" else "입력해 주세요",
        helperText = if (isError) {
            "오류 상태 테스트입니다"
        } else {
            when (type) {
                MjTextFieldType.Text -> "이름을 입력해 주세요"
                MjTextFieldType.Dropdown -> "언어 목록에서 선택해 주세요"
                MjTextFieldType.Date -> "날짜를 선택해 주세요"
            }
        },
        isError = isError,
        onClick = { if (type == MjTextFieldType.Date) showDatePicker = true },
    )

    if (showDatePicker) {
        val initialDate = remember(value) {
            runCatching {
                LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }.getOrNull()
        }
        val dateState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                val selectedMillis = dateState.selectedDateMillis
                Text(
                    text = "확인",
                    color = if (selectedMillis != null) colors.textBrand else colors.textDisabled,
                    style = MjHackathonTheme.typography.buttonM,
                    modifier = Modifier
                        .then(
                            if (selectedMillis != null) {
                                Modifier.noRippleClickable {
                                    value = Instant.ofEpochMilli(selectedMillis)
                                        .atZone(ZoneOffset.UTC)
                                        .toLocalDate()
                                        .toString()
                                    showDatePicker = false
                                }
                            } else {
                                Modifier
                            },
                        )
                        .padding(16.dp),
                )
            },
            dismissButton = {
                Text(
                    text = "취소",
                    color = colors.textSecondary,
                    style = MjHackathonTheme.typography.buttonM,
                    modifier = Modifier
                        .noRippleClickable { showDatePicker = false }
                        .padding(16.dp),
                )
            },
        ) {
            DatePicker(state = dateState)
        }
    }
}
