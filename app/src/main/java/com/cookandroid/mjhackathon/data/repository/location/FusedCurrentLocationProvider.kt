package com.cookandroid.mjhackathon.data.repository.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

class FusedCurrentLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : CurrentLocationProvider {
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): QuestCoordinates {
        val fine = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!fine && !coarse) throw LocationUnavailableException()
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!LocationManagerCompat.isLocationEnabled(manager)) throw LocationUnavailableException()
        return withTimeoutOrNull(20_000L) {
            suspendCancellableCoroutine { continuation ->
                val token = CancellationTokenSource()
                continuation.invokeOnCancellation { token.cancel() }
                try {
                    LocationServices.getFusedLocationProviderClient(context)
                        .getCurrentLocation(
                            if (fine) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            token.token,
                        )
                        .addOnSuccessListener { location ->
                            if (continuation.isActive) {
                                if (location == null) continuation.resumeWithException(LocationUnavailableException())
                                else continuation.resume(QuestCoordinates(location.latitude, location.longitude))
                            }
                        }
                        .addOnFailureListener { error ->
                            if (continuation.isActive) continuation.resumeWithException(LocationUnavailableException(error))
                        }
                        .addOnCanceledListener {
                            if (continuation.isActive) continuation.resumeWithException(LocationUnavailableException())
                        }
                } catch (error: SecurityException) {
                    if (continuation.isActive) continuation.resumeWithException(LocationUnavailableException(error))
                }
            }
        } ?: throw LocationUnavailableException()
    }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

class LocationUnavailableException(cause: Throwable? = null) : IOException("현재 위치를 확인할 수 없어요.", cause)
