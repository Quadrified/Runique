package com.quadrified.run.presentation.run_overview.model

// Same as Run data class in "core/domain/run"
// Formatted UI display
data class RunUi(
    val id: String,
    val duration: String,
    val dateTime: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String,
    val totalElevation: String,
    val mapPictureUrl: String?,
)