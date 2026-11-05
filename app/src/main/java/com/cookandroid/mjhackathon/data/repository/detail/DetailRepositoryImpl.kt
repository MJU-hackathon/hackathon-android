package com.cookandroid.mjhackathon.data.repository.detail

import com.cookandroid.mjhackathon.data.model.detail.QuestDetail
import com.cookandroid.mjhackathon.data.service.detail.DetailService
import jakarta.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val detailService: DetailService,
) : DetailRepository {
    override suspend fun getQuestDetail(questId: String): QuestDetail {
        // API 연결 지점: detailService의 실제 조회 함수에 questId를 전달하고 DTO를 변환한다.
        // 경로/필드가 미확정이라 기존 예제 API를 임의로 호출하지 않는다.
        return requireNotNull(mockDetails[questId]) { "퀘스트를 찾을 수 없어요." }
    }

    override suspend fun passQuest(questId: String) {
        // API 연결 지점: 실제 패스 요청이 성공했을 때만 정상 반환한다. 실패는 예외로 전달한다.
        require(mockDetails.containsKey(questId)) { "퀘스트를 찾을 수 없어요." }
    }

    private val mockDetails = listOf(
        QuestDetail(
            id = "mock-food",
            category = "음식",
            title = "시장에서 만나는 첫 번째 한입",
            description = "서대문 영천시장에서 한국 시장 음식을 직접 골라 주문해 보는 퀘스트예요.",
            steps = listOf("시장에서 먹어보고 싶은 음식 찾기", "한국어로 주문해 보기", "음식이나 시장 풍경 사진 찍기"),
            placeName = "영천시장",
            address = "서대문구 영천동",
            latitude = 37.5704,
            longitude = 126.9624,
        ),
        QuestDetail(
            id = "mock-drama",
            category = "드라마",
            title = "나만의 드라마 같은 골목",
            description = "신촌 연세로를 걸으며 드라마의 한 장면 같은 나만의 장소를 찾아보세요.",
            steps = listOf("연세로 골목 천천히 둘러보기", "마음에 드는 장소 찾기", "나만의 드라마 장면 사진 찍기"),
            placeName = "신촌 연세로",
            address = "서대문구 창천동",
            latitude = 37.5588,
            longitude = 126.9368,
        ),
        QuestDetail(
            id = "mock-tour",
            category = "관광",
            title = "독립문 앞, 서울의 한 장면",
            description = "독립문 주변을 산책하고 서울의 역사와 오늘이 만나는 장면을 남겨보세요.",
            steps = listOf("독립문 주변 산책하기", "안내판에서 역사 알아보기", "기억에 남는 풍경 사진 찍기"),
            placeName = "독립문",
            address = "서대문구 현저동",
            latitude = 37.5724,
            longitude = 126.9595,
        ),
    ).associateBy { it.id }
}
