package com.cookandroid.mjhackathon.presentation.profile

import java.time.LocalDate

enum class ProfileDateField { BIRTHDAY, STAY_START, STAY_END }

data class ProfileState(
    val nickname: String = "",
    val language: String = "한국어",
    val birthday: String = "",
    val stayStart: String = "",
    val stayEnd: String = "",
    val interests: Set<String> = emptySet(),
    val languageMenuExpanded: Boolean = false,
    val activeDateField: ProfileDateField? = null,
) {
    val nicknameError: Boolean
        get() = nickname.isNotEmpty() && nickname.trim().length !in 2..12

    val stayDateError: Boolean
        get() {
            val start = parseDate(stayStart) ?: return false
            val end = parseDate(stayEnd) ?: return false
            return end.isBefore(start)
        }

    val canSave: Boolean
        get() = nickname.trim().length in 2..12 &&
                language.isNotBlank() && parseDate(birthday) != null &&
                parseDate(stayStart) != null && parseDate(stayEnd) != null &&
                !stayDateError && interests.isNotEmpty()
}

private fun parseDate(value: String): LocalDate? =
    runCatching { LocalDate.parse(value) }.getOrNull()
