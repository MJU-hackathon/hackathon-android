package com.cookandroid.mjhackathon.presentation.profile

import java.time.LocalDate
import com.cookandroid.mjhackathon.data.dto.profile.request.SaveProfileRequest

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
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val saveErrorMessage: String? = null,
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
                language in profileLocales && parseDate(birthday) != null &&
                parseDate(stayStart) != null && parseDate(stayEnd) != null &&
                !stayDateError && interests.isNotEmpty() && interests.all { it in profileInterests }
}

internal val profileLocales = linkedMapOf(
    "한국어" to "ko", "English" to "en", "Tiếng Việt" to "vi", "中文" to "zh", "日本語" to "ja",
)

internal val profileInterests = linkedMapOf(
    "KPOP" to "KPOP", "드라마" to "DRAMA", "영화" to "MOVIE", "뷰티" to "BEAUTY",
    "음식" to "FOOD", "웹툰" to "WEBTOON", "게임" to "GAME", "패션" to "FASHION", "관광" to "TOURISM",
)

internal fun ProfileState.toSaveRequest(): SaveProfileRequest = SaveProfileRequest(
    nickname = nickname.trim(),
    locale = profileLocales.getValue(language),
    birthDate = birthday,
    stayStartDate = stayStart,
    stayEndDate = stayEnd,
    interests = interests.map { profileInterests.getValue(it) },
)

private fun parseDate(value: String): LocalDate? =
    runCatching { LocalDate.parse(value) }.getOrNull()
