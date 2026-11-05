package com.cookandroid.mjhackathon.presentation.detail

import com.cookandroid.mjhackathon.data.model.detail.QuestDetail

data class DetailUiState(
    val isLoading: Boolean = true,
    val quest: QuestDetail? = null,
    val loadError: String? = null,
    val isPassing: Boolean = false,
    val passError: String? = null,
    val passedQuestId: String? = null,
    val actionsUnavailable: Boolean = false,
) {
    val canAct: Boolean
        get() = !isLoading && quest?.status == "AVAILABLE" && !isPassing &&
            !actionsUnavailable && passedQuestId == null
}
