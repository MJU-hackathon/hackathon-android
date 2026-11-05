package com.cookandroid.mjhackathon.presentation.home

import com.cookandroid.mjhackathon.core.designsystem.component.MjStayCardData

data class HomeQuest(
    val id: String,
    val title: String,
    val category: String,
    val location: String,
    val estimatedMinutes: Int = 0,
    val imageUrl: String? = null,
    val reason: String = "",
)

data class HomeUiState(
    val stay: MjStayCardData = MjStayCardData(),
    val quests: List<HomeQuest> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val profileRequired: Boolean = false,
) {
    val remainingQuestCount: Int get() = quests.size

    companion object {
        const val DAILY_QUEST_COUNT = 3

        /** Preview 전용 샘플. 실제 실행에서는 서버 응답을 사용한다. */
        fun mock() = HomeUiState(
            isLoading = false,
            quests = listOf(
                HomeQuest("mock-food", "시장에서 만나는 첫 번째 한입", "음식", "영천시장"),
                HomeQuest("mock-drama", "나만의 드라마 같은 골목", "드라마", "신촌 연세로"),
                HomeQuest("mock-tour", "독립문 앞, 서울의 한 장면", "관광", "독립문"),
            ),
        )
    }
}
