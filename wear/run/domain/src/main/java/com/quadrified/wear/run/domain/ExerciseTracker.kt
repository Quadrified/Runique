package com.quadrified.wear.run.domain

import com.quadrified.core.domain.util.EmptyResult
import com.quadrified.core.domain.util.Error
import kotlinx.coroutines.flow.Flow

// For all Health services
// Implementation in "HealthServicesExerciseTracker"
interface ExerciseTracker {

    val heartRate: Flow<Int>

    suspend fun isHeartRateTrackingSupported(): Boolean

    suspend fun prepareExercise(): EmptyResult<ExerciseError>

    suspend fun startExercise(): EmptyResult<ExerciseError>

    suspend fun pauseExercise(): EmptyResult<ExerciseError>

    suspend fun resumeExercise(): EmptyResult<ExerciseError>

    suspend fun stopExercise(): EmptyResult<ExerciseError>
}

enum class ExerciseError : Error {
    TRACKING_NOT_SUPPORTED,
    ONGOING_OWN_EXERCISE,
    ONGOING_OTHER_EXERCISE,
    EXERCISE_ALREADY_ENDED,
    UNKNOWN
}