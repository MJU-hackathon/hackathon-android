package com.cookandroid.mjhackathon.data.repository.detail

import com.cookandroid.mjhackathon.data.model.detail.QuestDetail

interface DetailRepository {
    suspend fun getQuestDetail(questId: String): QuestDetail
    suspend fun passQuest(questId: String)
}
