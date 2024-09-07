package com.quadrified.run.presentation.run_overview.mapper

import com.quadrified.core.domain.run.Run
import com.quadrified.core.presentation.ui.formatted
import com.quadrified.core.presentation.ui.toFormattedKm
import com.quadrified.core.presentation.ui.toFormattedKmh
import com.quadrified.core.presentation.ui.toFormattedMeters
import com.quadrified.core.presentation.ui.toFormattedPace
import com.quadrified.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// To map Run (core/domain/run) => RunUi (run/presentation/run_overview/model)

fun Run.toRunUi(): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl

    )
}