package com.quadrified.run.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.quadrified.core.domain.location.LocationWithAltitude
import com.quadrified.run.domain.LocationObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Implementation of LocationObserver in "run/domain"
class AndroidLocationObserver(
    private val context: Context
) : LocationObserver {
    // To track user's location
    private val client = LocationServices.getFusedLocationProviderClient(context)

    override fun observeLocation(interval: Long): Flow<LocationWithAltitude> {
        /**
         * callbackFlow => used when call back function emits value
         * Used when interacting with an API that provides events or data through callbacks or listeners (like location updates, sensor events, etc.).
         * trySend(data): This function is used to emit values into the flow safely from within the callback. trySend returns a result indicating whether the emission was successful.
         * awaitClose {}: This function is used to handle any clean-up, like unregistering the callback or listener when the flow collection is stopped (i.e., when the coroutine is cancelled). It ensures that you properly release any resources related to the callback when the flow is no longer active.
         */
        return callbackFlow {
            // !! => “not-null assertion operator” to assert that an expression that could potentially be null is not null. If the value is null, a NullPointerException will be thrown.
            val locationManager = context.getSystemService<LocationManager>()!!
            var isGpsEnabled = false
            var isNetworkEnabled = false

            // Assert values to be false, assign values, resume location updates
            while (!isGpsEnabled && !isNetworkEnabled) {
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (!isGpsEnabled && !isNetworkEnabled) {
                    delay(3000L)
                }
            }

            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                close()
            } else {
                client.lastLocation.addOnSuccessListener {
                    it?.let { location ->
                        trySend(location.toLocationWithAltitude())
                    }
                }

                val request =
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)

                        result.locations.lastOrNull()?.let { location ->
                            trySend(location.toLocationWithAltitude())
                        }
                    }
                }

                // triggered when there's new location data
                client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

                // awaitClose => closed when listening is closed in ViewModel or popping from Backstack
                awaitClose {
                    client.removeLocationUpdates(locationCallback)
                }
            }

        }
    }
}