package com.cookandroid.mjhackathon.presentation.home

import com.cookandroid.mjhackathon.core.designsystem.component.MjStayCardData

data class HomeQuest(
    val id: String,
    val title: String,
    val category: String,
    val location: String,
)

data class HomeUiState(
    val stay: MjStayCardData = MjStayCardData(),
    val quests: List<HomeQuest> = emptyList(),
) {
    val remainingQuestCount: Int get() = quests.size

    companion object {
        const val DAILY_QUEST_COUNT = 3

        /** 홈 API 명세가 준비되기 전 사용하는 화면 샘플. */
        fun mock() = HomeUiState(
            quests = listOf(
                HomeQuest("mock-food", "시장에서 만나는 첫 번째 한입", "음식", "영천시장"),
                HomeQuest("mock-drama", "나만의 드라마 같은 골목", "드라마", "신촌 연세로"),
                HomeQuest("mock-tour", "독립문 앞, 서울의 한 장면", "관광", "독립문"),
            ),
        )
    }
}
