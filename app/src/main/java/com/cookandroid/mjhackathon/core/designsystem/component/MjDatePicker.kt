package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.core.util.noRippleClickable
import java.time.LocalDate
import java.time.Period
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlinx.coroutines.flow.collect

enum class MjStayDateField { Start, End }
enum class MjCalendarDayState { Default, Disabled, Selected, RangeStart, RangeMiddle, RangeEnd }

private val pickerDateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")
private fun pickerToday(): LocalDate = LocalDate.now(ZoneId.of("Asia/Seoul"))

/** 날짜는 확인할 때만 호출부로 전달한다. 취소·바깥 영역 터치는 입력값을 변경하지 않는다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MjBirthDatePicker(
    initialDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    today: LocalDate = pickerToday(),
) {
    MjPickerSheet(onDismiss) {
        MjBirthDatePickerContent(initialDate, onDismiss, onConfirm, today)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MjStayPeriodPicker(
    initialStart: LocalDate?,
    initialEnd: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit,
    initialField: MjStayDateField = MjStayDateField.Start,
    today: LocalDate = pickerToday(),
) {
    MjPickerSheet(onDismiss) {
        MjStayPeriodPickerContent(initialStart, initialEnd, onDismiss, onConfirm, initialField, today)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MjPickerSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MjHackathonTheme.colors.bgSurface,
        dragHandle = null,
    ) { content() }
}

@Composable
private fun MjPickerFrame(
    title: String, subtitle: String, onDismiss: () -> Unit, onConfirm: () -> Unit,
    confirmText: String = "확인", confirmEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MjHackathonTheme.colors
    Column(Modifier.fillMaxWidth().heightIn(max = LocalConfiguration.current.screenHeightDp.dp * .9f)
        .background(colors.bgSurface)) {
        Box(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 16.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(40.dp, 4.dp).clip(CircleShape).background(colors.borderDefault))
        }
        Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MjHackathonTheme.typography.title2, color = colors.textPrimary)
                Text(subtitle, style = MjHackathonTheme.typography.caption, color = colors.textSecondary)
            }
            content()
        }
        HorizontalDivider(color = colors.borderDefault.copy(alpha = .4f))
        Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.width(110.dp).heightIn(min = 52.dp).clip(RoundedCornerShape(16.dp))
                .border(1.dp, colors.borderDefault, RoundedCornerShape(16.dp))
                .semantics { role = Role.Button }.noRippleClickable(onDismiss), contentAlignment = Alignment.Center) {
                Text("취소", color = colors.textPrimary, style = MjHackathonTheme.typography.buttonL)
            }
            MjButton(confirmText, onConfirm, Modifier.weight(1f), enabled = confirmEnabled)
        }
    }
}

/** 휠은 가운데 행에 스냅하며 월·연도 변경으로 사라진 날짜는 마지막 유효일로 보정한다. */
@Composable
fun MjBirthDatePickerContent(
    initialDate: LocalDate?, onDismiss: () -> Unit, onConfirm: (LocalDate) -> Unit,
    today: LocalDate = pickerToday(),
) {
    val initial = (initialDate ?: today.minusYears(20)).coerceIn(LocalDate.of(1900, 1, 1), today)
    var dateText by rememberSaveable(initialDate?.toString()) { mutableStateOf(initial.toString()) }
    val date = LocalDate.parse(dateText)
    val age = Period.between(date, today).years
    var movingWheels by remember { mutableStateOf(emptySet<String>()) }
    MjPickerFrame("생년월일", "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일 · 만 ${age}세",
        onDismiss, { onConfirm(date) }, confirmEnabled = movingWheels.isEmpty()) {
        Box(Modifier.fillMaxWidth().height(220.dp)) {
            Box(Modifier.align(Alignment.Center).fillMaxWidth().height(44.dp)
                .clip(RoundedCornerShape(12.dp)).background(MjHackathonTheme.colors.bgMuted))
            Row(Modifier.fillMaxSize()) {
                MjDateWheel(1900..today.year, date.year, "년", "연도", Modifier.weight(1.2f),
                    onScrolling = { movingWheels = if (it) movingWheels + "year" else movingWheels - "year" }) {
                    dateText = clampBirthDate(it, date.monthValue, date.dayOfMonth, today).toString()
                }
                MjDateWheel(1..if (date.year == today.year) today.monthValue else 12,
                    date.monthValue, "월", "월", Modifier.weight(1f),
                    onScrolling = { movingWheels = if (it) movingWheels + "month" else movingWheels - "month" }) {
                    dateText = clampBirthDate(date.year, it, date.dayOfMonth, today).toString()
                }
                val maxDay = if (date.year == today.year && date.monthValue == today.monthValue)
                    today.dayOfMonth else date.lengthOfMonth()
                MjDateWheel(1..maxDay, date.dayOfMonth, "일", "일", Modifier.weight(1f),
                    onScrolling = { movingWheels = if (it) movingWheels + "day" else movingWheels - "day" }) {
                    dateText = clampBirthDate(date.year, date.monthValue, it, today).toString()
                }
            }
        }
    }
}

internal fun clampBirthDate(year: Int, month: Int, day: Int, today: LocalDate): LocalDate {
    val validYear = year.coerceIn(1900, today.year)
    val validMonth = month.coerceIn(1, if (validYear == today.year) today.monthValue else 12)
    val maxDay = YearMonth.of(validYear, validMonth).lengthOfMonth()
    return LocalDate.of(validYear, validMonth, day.coerceIn(1, maxDay)).coerceAtMost(today)
}

@Composable
private fun MjDateWheel(
    values: IntRange, value: Int, suffix: String, label: String,
    modifier: Modifier = Modifier, onScrolling: (Boolean) -> Unit = {}, onSelect: (Int) -> Unit,
) {
    val list = remember(values) { values.toList() }
    val state = rememberLazyListState(initialFirstVisibleItemIndex = (value - values.first).coerceIn(list.indices))
    val select by rememberUpdatedState(onSelect)
    val currentValue by rememberUpdatedState(value)
    val notifyScrolling by rememberUpdatedState(onScrolling)
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }.collect { notifyScrolling(it) }
    }
    LaunchedEffect(value, values) {
        if (!state.isScrollInProgress) state.scrollToItem((value - values.first).coerceIn(list.indices))
    }
    LaunchedEffect(state, values) {
        snapshotFlow {
            if (state.isScrollInProgress) null else {
                val info = state.layoutInfo
                val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
                info.visibleItemsInfo.minByOrNull { abs(it.offset + it.size / 2 - center) }?.index
            }
        }.collect { index ->
            list.getOrNull(index ?: -1)?.let { if (it != currentValue) select(it) }
        }
    }
    LazyColumn(
        state = state, modifier = modifier.height(220.dp).semantics { contentDescription = "$label 선택" },
        contentPadding = PaddingValues(vertical = 88.dp),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
    ) {
        items(list, key = { it }) { number ->
            val distance = abs(number - value)
            Box(Modifier.fillMaxWidth().height(44.dp)
                .semantics { role = Role.Button; selected = number == value }
                .noRippleClickable { select(number) }, contentAlignment = Alignment.Center) {
                Text("$number$suffix", style = if (number == value) MjHackathonTheme.typography.body1Strong
                    else MjHackathonTheme.typography.body1,
                    color = when { distance == 0 -> MjHackathonTheme.colors.textPrimary
                        distance == 1 -> MjHackathonTheme.colors.textSecondary
                        else -> MjHackathonTheme.colors.textDisabled.copy(alpha = .5f) })
            }
        }
    }
}

@Composable
fun MjStayPeriodPickerContent(
    initialStart: LocalDate?, initialEnd: LocalDate?,
    onDismiss: () -> Unit, onConfirm: (LocalDate, LocalDate) -> Unit,
    initialField: MjStayDateField = MjStayDateField.Start, today: LocalDate = pickerToday(),
) {
    var startText by rememberSaveable { mutableStateOf(initialStart?.toString()) }
    var endText by rememberSaveable { mutableStateOf(initialEnd?.takeIf { initialStart == null || !it.isBefore(initialStart) }?.toString()) }
    var field by rememberSaveable { mutableStateOf(initialField) }
    var monthText by rememberSaveable { mutableStateOf(YearMonth.from(
        (if (initialField == MjStayDateField.End) initialEnd else initialStart) ?: initialStart ?: today).toString()) }
    val start = startText?.let(LocalDate::parse)
    val end = endText?.let(LocalDate::parse)
    val month = YearMonth.parse(monthText)
    val subtitle = if (start != null && end != null)
        "${start.format(pickerDateFormat)} – ${end.format(pickerDateFormat)} · ${stayPeriodDays(start, end)}일"
        else "시작일과 종료일을 선택해 주세요"
    MjPickerFrame("한국 체류 기간", subtitle, onDismiss,
        { if (start != null && end != null) onConfirm(start, end) }, "선택 완료",
        confirmEnabled = start != null && end != null && !end.isBefore(start)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            MjPeriodField("시작일", start, field == MjStayDateField.Start, Modifier.weight(1f)) {
                field = MjStayDateField.Start
                start?.let { monthText = YearMonth.from(it).toString() }
            }
            Icon(painterResource(R.drawable.ic_right_arrow), null, Modifier.size(16.dp), tint = MjHackathonTheme.colors.iconDefault)
            MjPeriodField("종료일", end, field == MjStayDateField.End, Modifier.weight(1f)) {
                field = MjStayDateField.End
                (end ?: start)?.let { monthText = YearMonth.from(it).toString() }
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            MjMonthArrow("이전 달", enabled = month > YearMonth.of(1, 1)) { monthText = month.minusMonths(1).toString() }
            Text("${month.year}년 ${month.monthValue}월", Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                style = MjHackathonTheme.typography.body1Strong, color = MjHackathonTheme.colors.textPrimary)
            MjMonthArrow("다음 달", next = true, enabled = month < YearMonth.of(9999, 12)) { monthText = month.plusMonths(1).toString() }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth()) {
                listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                    Text(it, Modifier.weight(1f).padding(bottom = 12.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MjHackathonTheme.typography.caption, color = MjHackathonTheme.colors.textTertiary)
                }
            }
            calendarMonthCells(month).chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        if (date == null) Spacer(Modifier.weight(1f).height(40.dp))
                        else {
                            val enabled = field != MjStayDateField.End || start == null || !date.isBefore(start)
                            val dayState = when {
                                !enabled -> MjCalendarDayState.Disabled
                                date == start && date == end -> MjCalendarDayState.Selected
                                date == start && end == null -> MjCalendarDayState.Selected
                                date == end && start == null -> MjCalendarDayState.Selected
                                date == start -> MjCalendarDayState.RangeStart
                                date == end -> MjCalendarDayState.RangeEnd
                                start != null && end != null && date > start && date < end -> MjCalendarDayState.RangeMiddle
                                else -> MjCalendarDayState.Default
                            }
                            MjCalendarDay(date.dayOfMonth, dayState, Modifier.weight(1f), date == today,
                                date.toString()) {
                                if (field == MjStayDateField.Start || start == null) {
                                    startText = date.toString()
                                    if (end != null && end.isBefore(date)) endText = null
                                    field = MjStayDateField.End
                                } else endText = date.toString()
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

internal fun stayPeriodDays(start: LocalDate, end: LocalDate): Long = ChronoUnit.DAYS.between(start, end) + 1

internal fun calendarMonthCells(month: YearMonth): List<LocalDate?> {
    val offset = month.atDay(1).dayOfWeek.value % 7
    val length = ((offset + month.lengthOfMonth() + 6) / 7) * 7
    return List(length) { index -> (index - offset + 1).takeIf { it in 1..month.lengthOfMonth() }?.let(month::atDay) }
}

@Composable
private fun MjPeriodField(label: String, date: LocalDate?, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val colors = MjHackathonTheme.colors
    Column(modifier.clip(RoundedCornerShape(12.dp)).background(if (active) colors.bgBrandSubtle else colors.bgSurface)
        .border(if (active) 2.dp else 1.dp, if (active) colors.borderBrand else colors.borderDefault, RoundedCornerShape(12.dp))
        .semantics { role = Role.Button; selected = active; contentDescription = "$label 선택" }
        .noRippleClickable(onClick).padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(if (active) "$label · 선택 중" else label, style = MjHackathonTheme.typography.caption,
            color = if (active) colors.textBrand else colors.textTertiary)
        Text(date?.format(pickerDateFormat) ?: "날짜 선택", style = MjHackathonTheme.typography.body2Strong, color = colors.textPrimary)
    }
}

@Composable
private fun MjMonthArrow(label: String, next: Boolean = false, enabled: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(44.dp).semantics { role = Role.Button; contentDescription = label; if (!enabled) disabled() }
        .then(if (enabled) Modifier.noRippleClickable(onClick) else Modifier), contentAlignment = Alignment.Center) {
        Icon(painterResource(R.drawable.ic_right_arrow), null, Modifier.size(20.dp).rotate(if (next) 0f else 180f),
            tint = if (enabled) MjHackathonTheme.colors.iconDefault else MjHackathonTheme.colors.textDisabled)
    }
}

@Composable
fun MjCalendarDay(
    day: Int, state: MjCalendarDayState, modifier: Modifier = Modifier,
    isToday: Boolean = false, accessibilityLabel: String = "${day}일", onClick: () -> Unit = {},
) {
    val colors = MjHackathonTheme.colors
    val endpoint = state in setOf(MjCalendarDayState.Selected, MjCalendarDayState.RangeStart, MjCalendarDayState.RangeEnd)
    val range = state in setOf(MjCalendarDayState.RangeStart, MjCalendarDayState.RangeMiddle, MjCalendarDayState.RangeEnd)
    Box(modifier.height(40.dp).semantics {
        role = Role.Button; contentDescription = accessibilityLabel
        selected = endpoint
        if (state == MjCalendarDayState.Disabled) disabled()
    }.then(if (state != MjCalendarDayState.Disabled) Modifier.noRippleClickable(onClick) else Modifier), contentAlignment = Alignment.Center) {
        if (range) {
            val band = when (state) {
                MjCalendarDayState.RangeStart -> Modifier.fillMaxWidth(.5f).align(Alignment.CenterEnd)
                MjCalendarDayState.RangeEnd -> Modifier.fillMaxWidth(.5f).align(Alignment.CenterStart)
                else -> Modifier.fillMaxWidth()
            }
            Box(band.height(40.dp).background(colors.bgBrandSubtle))
        }
        if (endpoint) Box(Modifier.size(40.dp).clip(CircleShape).background(colors.actionPrimary))
        Text(day.toString(), style = if (endpoint) MjHackathonTheme.typography.body2Strong else MjHackathonTheme.typography.body2,
            color = when { state == MjCalendarDayState.Disabled -> colors.textDisabled
                isToday && !endpoint -> colors.textBrand else -> colors.textPrimary })
        if (isToday && !endpoint) Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp)
            .size(3.dp).clip(CircleShape).background(colors.actionPrimary))
    }
}

@Preview(name = "생년월일 - 휠", widthDp = 355, showBackground = true)
@Composable
private fun BirthPickerPreview() {
    MjHackathonTheme { MjBirthDatePickerContent(LocalDate.of(1998, 5, 17), {}, {}, LocalDate.of(2026, 11, 6)) }
}

@Preview(name = "체류 기간 - 종료일 선택", widthDp = 355, showBackground = true)
@Composable
private fun StayPickerPreview() {
    MjHackathonTheme { MjStayPeriodPickerContent(LocalDate.of(2026, 9, 1), LocalDate.of(2027, 2, 28), {}, { _, _ -> }, MjStayDateField.End, LocalDate.of(2026, 11, 6)) }
}

@Preview(name = "체류 기간 - 빈 선택", widthDp = 355, showBackground = true)
@Composable
private fun StayPickerEmptyPreview() {
    MjHackathonTheme { MjStayPeriodPickerContent(null, null, {}, { _, _ -> }, today = LocalDate.of(2026, 11, 6)) }
}

@Preview(name = "체류 기간 - 같은 날", widthDp = 355, showBackground = true)
@Composable
private fun StayPickerSameDayPreview() {
    MjHackathonTheme { MjStayPeriodPickerContent(LocalDate.of(2026, 11, 6), LocalDate.of(2026, 11, 6), {}, { _, _ -> }, today = LocalDate.of(2026, 11, 6)) }
}

@Preview(name = "체류 기간 - 시작일만 선택", widthDp = 355, showBackground = true)
@Composable
private fun StayPickerStartOnlyPreview() {
    MjHackathonTheme { MjStayPeriodPickerContent(LocalDate.of(2026, 11, 6), null, {}, { _, _ -> }, MjStayDateField.End, LocalDate.of(2026, 11, 6)) }
}

@Preview(name = "CalendarDay - 기본·오늘·비활성·선택·기간", widthDp = 355, showBackground = true)
@Composable
private fun CalendarDayPreview() {
    MjHackathonTheme {
        Row(Modifier.background(MjHackathonTheme.colors.bgSurface).padding(8.dp)) {
            MjCalendarDay(15, MjCalendarDayState.Default, Modifier.weight(1f))
            MjCalendarDay(15, MjCalendarDayState.Default, Modifier.weight(1f), isToday = true)
            MjCalendarDay(15, MjCalendarDayState.Disabled, Modifier.weight(1f))
            MjCalendarDay(15, MjCalendarDayState.Selected, Modifier.weight(1f))
            MjCalendarDay(15, MjCalendarDayState.RangeStart, Modifier.weight(1f))
            MjCalendarDay(15, MjCalendarDayState.RangeMiddle, Modifier.weight(1f))
            MjCalendarDay(15, MjCalendarDayState.RangeEnd, Modifier.weight(1f))
        }
    }
}
