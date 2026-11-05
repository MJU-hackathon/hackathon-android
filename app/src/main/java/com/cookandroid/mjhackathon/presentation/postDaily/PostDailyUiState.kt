package com.cookandroid.mjhackathon.presentation.postDaily

import com.cookandroid.mjhackathon.data.model.postDaily.DiaryResult

data class PostDailyUiState(
    val questId: String = "",
    val questTitle: String = "",
    val location: String = "",
    val photoUri: String = "",
    val hasPhoto: Boolean = false,
    val result: DiaryResult? = null,
    val memo: String = "",
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val isSaving: Boolean = false,
    val isCancelling: Boolean = false,
    val saveError: String? = null,
    val saveBlocked: Boolean = false,
    val savedDiaryId: String? = null,
    val isCancelled: Boolean = false,
) {
    val memoLength: Int get() = memo.codePointCount(0, memo.length)
    val isBusy: Boolean get() = isSaving || isCancelling
    val canSave: Boolean get() = !isLoading && loadError == null && !isBusy &&
            savedDiaryId == null && !isCancelled && !saveBlocked && result != null && hasPhoto && memoLength <= MAX_MEMO_LENGTH

    companion object {
        const val MAX_MEMO_LENGTH = 50
    }
}

internal fun limitDiaryMemo(value: String): String {
    val singleLine = value.replace('\n', ' ').replace('\r', ' ')
    val count = singleLine.codePointCount(0, singleLine.length)
    return if (count > PostDailyUiState.MAX_MEMO_LENGTH) {
        singleLine.substring(0, singleLine.offsetByCodePoints(0, PostDailyUiState.MAX_MEMO_LENGTH))
    } else singleLine
}
