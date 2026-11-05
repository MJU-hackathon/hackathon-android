package com.cookandroid.mjhackathon.data.repository.detail

import com.cookandroid.mjhackathon.data.model.detail.QuestDetail
import com.cookandroid.mjhackathon.data.network.toApiException
import com.cookandroid.mjhackathon.data.service.detail.DetailService
import jakarta.inject.Inject
import retrofit2.HttpException

class DetailRepositoryImpl @Inject constructor(
    private val detailService: DetailService,
) : DetailRepository {
    override suspend fun getQuestDetail(questId: String, locale: String?): QuestDetail {
        val id = questId.toQuestId()
        val response = try { detailService.getQuestDetail(id, locale).data }
        catch (error: HttpException) { throw error.toApiException() }
        check(response.questId == id) { "퀘스트 정보가 일치하지 않습니다." }
        return QuestDetail(
            id = response.questId.toString(), category = response.category,
            title = response.title, description = response.description, steps = response.steps,
            placeName = response.placeName, address = response.address,
            latitude = response.latitude, longitude = response.longitude,
            imageUrl = response.imageUrl, estimatedMinutes = response.estimatedMinutes,
            reason = response.reason, locale = response.locale, status = response.status,
        )
    }

    override suspend fun passQuest(questId: String) {
        val id = questId.toQuestId()
        val response = try { detailService.passQuest(id).data }
        catch (error: HttpException) { throw error.toApiException() }
        // HTTP 성공만으로 홈에서 제거하지 않고 실제 패스 결과를 확인한다.
        check(response.questId == id && response.status == "SKIPPED") {
            "퀘스트 패스 결과를 확인하지 못했어요."
        }
    }
}

private fun String.toQuestId(): Long =
    requireNotNull(toLongOrNull()?.takeIf { it > 0 }) { "올바른 퀘스트 ID가 아닙니다." }
