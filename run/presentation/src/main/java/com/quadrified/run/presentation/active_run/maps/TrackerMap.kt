@file:OptIn(MapsComposeExperimentalApi::class, DelicateCoroutinesApi::class)

package com.quadrified.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.ktx.awaitSnapshot
import com.quadrified.core.domain.location.Location
import com.quadrified.core.domain.location.LocationTimestamp
import com.quadrified.core.presentation.designsystem.RunIcon
import com.quadrified.run.presentation.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TrackerMap(
    modifier: Modifier = Modifier,
    isRunFinished: Boolean,
    currentLocation: Location?,
    locations: List<List<LocationTimestamp>>,
    onSnapShot: (Bitmap) -> Unit,
) {
    val context = LocalContext.current

    /**
     * Customised at: https://mapstyle.withgoogle.com/
     */
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState()

    val markerPositionLat by animateFloatAsState(
        targetValue = currentLocation?.lat?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )
    val markerPositionLong by animateFloatAsState(
        targetValue = currentLocation?.long?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )
    // Google Map's version of Lat and Long
    val markerPosition =
        remember(LatLng(markerPositionLat.toDouble(), markerPositionLong.toDouble())) {
            LatLng(markerPositionLat.toDouble(), markerPositionLong.toDouble())
        }

    // If run is not finished, keep updating marker state to new value
    LaunchedEffect(markerPosition, isRunFinished) {
        if (!isRunFinished) {
            markerState.position = markerPosition
        }

    }

    // Move marker camera position at the center of map
    LaunchedEffect(currentLocation, isRunFinished) {
        if (currentLocation != null && !isRunFinished) {
            val latLng = LatLng(currentLocation.lat, currentLocation.long)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            )
        }
    }

    var triggerCapture by remember {
        mutableStateOf(false)
    }
    // To create only one snapshot
    var createSnapshotJob: Job? = remember { null }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = mapStyle
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false
        ),
        modifier = if (isRunFinished) {
            modifier
                .width(300.dp)
                .aspectRatio(16 / 9f)
                .alpha(0f)
                .onSizeChanged {
                    if (it.width >= 300) {
                        triggerCapture = true
                    }
                }
        } else modifier,
    ) {
        RuniquePolylines(locations = locations)

        // To take snapshot of map
        MapEffect(locations, isRunFinished, triggerCapture, createSnapshotJob) { map ->
            if (isRunFinished && triggerCapture && createSnapshotJob == null) {
                triggerCapture = false

                // Area of polyline
                val boundsBuilder = LatLngBounds.builder()
                locations.flatten().forEach { location ->
                    boundsBuilder.include(
                        LatLng(
                            // 1st location LocationTimeStamp
                            // 2nd location LocationWithAltitude
                            // 3rd location Location(lat, long)
                            location.location.location.lat,
                            location.location.location.long,
                        )
                    )
                }

                map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        100
                    )
                )

                map.setOnCameraIdleListener {
                    createSnapshotJob?.cancel()
                    createSnapshotJob = GlobalScope.launch {
                        // To make sure the map is sharp and focus
                        // before taking screenshot
                        delay(500L)
                        map.awaitSnapshot()?.let(onSnapShot)
                    }
                }
            }
        }

        if (!isRunFinished && currentLocation != null) {
            MarkerComposable(
                // Updates when currentLocation changes
                currentLocation,
                state = markerState,
            ) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RunIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

            }
        }
    }
}
