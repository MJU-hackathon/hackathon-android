package com.cookandroid.mjhackathon.core.designsystem.component

import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.*
import org.junit.Test

class MjDatePickerTest {
    @Test fun clampsLeapDayWhenYearChanges() {
        val today = LocalDate.of(2026, 11, 6)
        assertEquals(LocalDate.of(2024, 2, 29), clampBirthDate(2024, 2, 31, today))
        assertEquals(LocalDate.of(2025, 2, 28), clampBirthDate(2025, 2, 29, today))
        assertEquals(today, clampBirthDate(2026, 12, 31, today))
    }

    @Test fun rangeDurationIncludesBothEndpoints() {
        val start = LocalDate.of(2026, 9, 1)
        assertEquals(181L, stayPeriodDays(start, LocalDate.of(2027, 2, 28)))
        assertEquals(1L, stayPeriodDays(start, start))
    }

    @Test fun sundayFirstCalendarHandlesLeapAndSixWeekMonths() {
        val february = calendarMonthCells(YearMonth.of(2027, 2))
        assertNull(february.first())
        assertEquals(LocalDate.of(2027, 2, 1), february[1])
        assertEquals(28, february.filterNotNull().size)
        assertEquals(29, calendarMonthCells(YearMonth.of(2024, 2)).filterNotNull().size)
        val sixWeeks = calendarMonthCells(YearMonth.of(2026, 5))
        assertEquals(42, sixWeeks.size)
        assertEquals(LocalDate.of(2026, 5, 1), sixWeeks[5])
    }
}
