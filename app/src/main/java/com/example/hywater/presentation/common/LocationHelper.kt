package com.example.hywater.presentation.common

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper around FusedLocationProviderClient that exposes a suspend function.
 *
 * Callers must hold ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION before calling
 * [getLastKnownOrCurrentLocation]; this class does NOT request permissions.
 */
@Singleton
class LocationHelper @Inject constructor() {

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownOrCurrentLocation(context: Context): Location? {
        val client = LocationServices.getFusedLocationProviderClient(context)

        // Try the cheap "last known" call first
        val last = client.lastLocation.await()
        if (last != null) return last

        // Fall back to a fresh single-shot location request
        val cts = CancellationTokenSource()
        return try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
        } finally {
            cts.cancel()
        }
    }
}
