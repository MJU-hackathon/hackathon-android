package com.cookandroid.mjhackathon.data.repository.postDaily

import android.content.Context
import android.net.Uri
import com.cookandroid.mjhackathon.data.model.postDaily.CreateDiaryRequest
import com.cookandroid.mjhackathon.data.model.postDaily.SavedDiary
import com.cookandroid.mjhackathon.data.network.toApiException
import com.cookandroid.mjhackathon.data.service.postDaily.PostDailyService
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import retrofit2.HttpException

/** 촬영 사진을 스트리밍 업로드한다. 일기 데이터는 로컬에 저장하지 않는다. */
class PostDailyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val postDailyService: PostDailyService,
) : PostDailyRepository {

    override suspend fun isPhotoAvailable(photoUri: String): Boolean = withContext(Dispatchers.IO) {
        val file = temporaryPhoto(photoUri) ?: return@withContext false
        file.isFile && file.length() > 0L &&
            context.contentResolver.openInputStream(Uri.parse(photoUri))?.use { it.read() >= 0 } == true
    }

    override suspend fun saveDiary(request: CreateDiaryRequest): SavedDiary = withContext(Dispatchers.IO) {
        saveMutex.withLock {
            val fields = request.toMultipartFields()
            val photo = createPhotoPart(request.photoUri)
            val response = try {
                postDailyService.createDiary(photo, fields.questId, fields.outcome, fields.memo,
                    fields.draftId, fields.draftStartedAt).data
            } catch (error: HttpException) { throw error.toApiException() }
            response.toSavedDiary(request.questId)
        }
    }

    private fun createPhotoPart(photoUri: String): MultipartBody.Part {
        val file = temporaryPhoto(photoUri)
            ?.takeIf { it.isFile && it.length() > 0L } ?: throw DiaryPhotoException("PHOTO_REQUIRED")
        val size = file.length()
        if (size > MAX_PHOTO_BYTES) throw DiaryPhotoException("FILE_TOO_LARGE")
        val uri = Uri.parse(photoUri)
        val mime = context.contentResolver.getType(uri)?.lowercase() ?: "image/jpeg"
        val extension = when (mime) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/heic" -> "heic"
            "image/heif" -> "heif"
            else -> throw DiaryPhotoException("INVALID_PHOTO")
        }
        val body = object : RequestBody() {
            override fun contentType() = mime.toMediaType()
            override fun contentLength() = size
            override fun writeTo(sink: BufferedSink) {
                val input = context.contentResolver.openInputStream(uri)
                    ?: throw IOException("촬영한 사진을 열지 못했어요.")
                input.source().use { sink.writeAll(it) }
            }
        }
        return MultipartBody.Part.createFormData("photo", "diary.$extension", body)
    }

    override suspend fun discardTemporaryPhoto(photoUri: String) = withContext(Dispatchers.IO) {
        temporaryPhoto(photoUri)?.let { file ->
            if (file.exists()) check(file.delete()) { "임시 사진을 정리하지 못했습니다." }
        }
        Unit
    }

    /** 이 앱이 생성한 촬영 캐시 파일만 업로드하거나 삭제한다. */
    private fun temporaryPhoto(photoUri: String): File? {
        val uri = Uri.parse(photoUri)
        if (uri.scheme != "content" || uri.authority != "${context.packageName}.questphotos") return null
        if (uri.pathSegments.size != 2 || uri.pathSegments.first() != "quest_photos") return null
        val directory = File(context.cacheDir, "quest_photos").canonicalFile
        val name = uri.lastPathSegment ?: return null
        val file = File(directory, name).canonicalFile
        return file.takeIf { it.parentFile == directory && it.name.startsWith("quest_") &&
            it.extension.lowercase() in setOf("jpg", "jpeg", "png", "heic", "heif") }
    }

    companion object {
        internal const val MAX_PHOTO_BYTES = 10L * 1024 * 1024
        private val saveMutex = Mutex()
    }
}

class DiaryPhotoException(val code: String) : IOException(code)
