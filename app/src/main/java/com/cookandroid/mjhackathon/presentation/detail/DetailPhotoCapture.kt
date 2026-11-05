package com.cookandroid.mjhackathon.presentation.detail

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal data class DetailPhotoCapture(val isBusy: Boolean, val launch: () -> Unit)

/** 기본 카메라를 사용한다. 카메라 취소는 상세 화면에 머물며 홈 목록을 변경하지 않는다. */
@Composable
internal fun rememberDetailPhotoCapture(
    questId: String,
    onPhotoCaptured: (String, String) -> Unit,
    onError: (String) -> Unit,
): DetailPhotoCapture {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingPath by rememberSaveable(questId) { mutableStateOf<String?>(null) }
    var preparing by remember(questId) { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val path = pendingPath
        pendingPath = null
        if (path != null) {
            preparing = true
            scope.launch {
                try {
                    val file = File(path)
                    val valid = withContext(Dispatchers.IO) {
                        if (success && file.exists() && file.length() > 0L) true
                        else { file.delete(); false }
                    }
                    if (valid) {
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.questphotos", file)
                        onPhotoCaptured(questId, uri.toString())
                    } else if (success) {
                        onError("촬영한 사진을 확인하지 못했어요. 다시 시도해 주세요.")
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    onError("촬영한 사진을 확인하지 못했어요. 다시 시도해 주세요.")
                } finally {
                    preparing = false
                }
            }
        }
    }
    return DetailPhotoCapture(isBusy = preparing || pendingPath != null) {
        if (!preparing && pendingPath == null &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            preparing = true
            scope.launch {
                try {
                    val file = withContext(Dispatchers.IO) {
                        val directory = File(context.cacheDir, "quest_photos")
                        check(directory.isDirectory || directory.mkdirs())
                        File.createTempFile("quest_", ".jpg", directory)
                    }
                    pendingPath = file.absolutePath
                    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.questphotos", file)
                    launcher.launch(uri)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    val path = pendingPath
                    pendingPath = null
                    withContext(Dispatchers.IO) { path?.let { File(it).delete() } }
                    onError("카메라를 열지 못했어요. 다시 시도해 주세요.")
                } finally {
                    preparing = false
                }
            }
        }
    }
}
