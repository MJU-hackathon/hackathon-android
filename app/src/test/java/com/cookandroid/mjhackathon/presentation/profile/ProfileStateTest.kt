package com.cookandroid.mjhackathon.presentation.profile

import com.cookandroid.mjhackathon.data.dto.base.BaseResponse
import com.cookandroid.mjhackathon.data.dto.profile.response.ProfileResponse
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ProfileStateTest {
    private val valid = ProfileState(
        nickname = " 하나 ", language = "Tiếng Việt", birthday = "2003-04-12",
        stayStart = "2026-03-01", stayEnd = "2027-02-28",
        interests = linkedSetOf("KPOP", "영화", "음식"),
    )

    @Test fun mapsFormToServerValues() {
        assertTrue(valid.canSave)
        val request = valid.toSaveRequest()
        assertEquals("하나", request.nickname)
        assertEquals("vi", request.locale)
        assertEquals(listOf("KPOP", "MOVIE", "FOOD"), request.interests)
        assertEquals("TOURISM", valid.copy(interests = setOf("관광")).toSaveRequest().interests.single())
        assertEquals(setOf("ko", "en", "vi", "zh", "ja"), profileLocales.values.toSet())
    }

    @Test fun validatesDatesAndRequiredFields() {
        assertTrue(valid.copy(stayEnd = valid.stayStart).canSave)
        assertFalse(valid.copy(stayEnd = "2026-02-28").canSave)
        assertFalse(valid.copy(birthday = "2003-02-29").canSave)
        assertFalse(valid.copy(nickname = "한").canSave)
        assertFalse(valid.copy(nickname = "가".repeat(13)).canSave)
        assertFalse(valid.copy(interests = emptySet()).canSave)
        assertFalse(valid.copy(interests = setOf("unknown")).canSave)
        assertFalse(valid.copy(language = "unknown").canSave)
    }

    @Test fun decodesServerResponse() {
        val response = Json.decodeFromString<BaseResponse<ProfileResponse>>(
            """{"status":200,"code":"SUCCESS","message":"ok","data":{"nickname":"하나","locale":"vi","birthDate":"2003-04-12","stayStartDate":"2026-03-01","stayEndDate":"2027-02-28","interests":["KPOP"],"dDay":114,"stayProgressPercent":69}}"""
        )
        assertEquals(200, response.status)
        assertEquals(114L, response.data.dDay)
        assertEquals(69, response.data.stayProgressPercent)
    }
}
