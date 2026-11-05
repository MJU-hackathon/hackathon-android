package com.cookandroid.mjhackathon.data.repository.postDaily

import android.content.Context
import android.net.Uri
import android.util.AtomicFile
import com.cookandroid.mjhackathon.data.model.postDaily.CreateDiaryRequest
import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary
import com.cookandroid.mjhackathon.data.service.postDaily.PostDailyService
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

/**
 * API 명세 확정 전 사용하는 실제 로컬 저장 구현.
 * API 연결 시 saveDiary 내부의 저장 부분을 Retrofit 업로드/저장 요청으로 교체한다.
 * URI 읽기, Multipart 변환 및 업로드는 이 Data 계층에서 처리한다.
 */
class PostDailyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val postDailyService: PostDailyService,
) : PostDailyRepository {
    private val json = Json { ignoreUnknownKeys = true }
    private val recordDirectory get() = File(context.filesDir, "diary_records")
    private val photoDirectory get() = File(context.filesDir, "diary_photos")

    override suspend fun isPhotoAvailable(photoUri: String): Boolean = withContext(Dispatchers.IO) {
        val file = temporaryPhoto(photoUri) ?: return@withContext false
        file.isFile && file.length() > 0L &&
                context.contentResolver.openInputStream(Uri.parse(photoUri))?.use { it.read() >= 0 } == true
    }

    override suspend fun saveDiary(request: CreateDiaryRequest): SavedDiary = withContext(Dispatchers.IO) {
        saveMutex.withLock {
            require(request.questId.isNotBlank())
            require(request.memo.codePointCount(0, request.memo.length) <= 50)
            val requestId = UUID.fromString(request.requestId).toString()
            check(recordDirectory.isDirectory || recordDirectory.mkdirs())
            check(photoDirectory.isDirectory || photoDirectory.mkdirs())
            val recordFile = File(recordDirectory, "$requestId.json")
            // 동일 요청 재전송 시 이미 저장한 결과를 반환해 중복 기록을 방지한다.
            if (recordFile.exists()) {
                return@withLock json.decodeFromString<SavedDiary>(recordFile.readText())
            }
            check(isPhotoAvailable(request.photoUri)) { "촬영한 사진이 없습니다." }
            val persistentPhoto = File(photoDirectory, "$requestId.jpg")
            val atomicRecord = AtomicFile(recordFile)
            var output: java.io.FileOutputStream? = null
            try {
                val input = requireNotNull(context.contentResolver.openInputStream(Uri.parse(request.photoUri)))
                input.use { source -> persistentPhoto.outputStream().use { source.copyTo(it) } }
                val saved = SavedDiary(
                    id = requestId,
                    questId = request.questId,
                    questTitle = request.questTitle,
                    location = request.location,
                    result = request.result,
                    photoUri = Uri.fromFile(persistentPhoto).toString(),
                    memo = request.memo,
                    savedAt = OffsetDateTime.now(ZoneId.of("Asia/Seoul")).toString(),
                )
                val stream = atomicRecord.startWrite()
                output = stream
                stream.write(json.encodeToString(saved).toByteArray(Charsets.UTF_8))
                atomicRecord.finishWrite(stream)
                saved
            } catch (error: Exception) {
                output?.let(atomicRecord::failWrite)
                persistentPhoto.delete()
                throw error
            }
        }
    }

    override suspend fun discardTemporaryPhoto(photoUri: String) = withContext(Dispatchers.IO) {
        temporaryPhoto(photoUri)?.let { file ->
            if (file.exists()) check(file.delete()) { "임시 사진을 정리하지 못했습니다." }
        }
        Unit
    }

    override suspend fun getSavedDiaries(): List<SavedDiary> = withContext(Dispatchers.IO) {
        saveMutex.withLock {
            recordDirectory.listFiles().orEmpty().filter { it.extension == "json" }
                .map { json.decodeFromString<SavedDiary>(it.readText()) }
                .sortedByDescending { it.savedAt }
        }
    }

    /** 이 앱이 촬영용으로 만든 cache/quest_photos의 파일만 읽거나 삭제한다. */
    private fun temporaryPhoto(photoUri: String): File? {
        val uri = Uri.parse(photoUri)
        if (uri.scheme != "content" || uri.authority != "${context.packageName}.questphotos") return null
        if (uri.pathSegments.size != 2 || uri.pathSegments.first() != "quest_photos") return null
        val directory = File(context.cacheDir, "quest_photos").canonicalFile
        val name = uri.lastPathSegment ?: return null
        val file = File(directory, name).canonicalFile
        return file.takeIf { it.parentFile == directory && it.name.startsWith("quest_") && it.extension == "jpg" }
    }

    companion object {
        private val saveMutex = Mutex()
    }
}
