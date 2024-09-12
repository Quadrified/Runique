package com.quadrified.core.domain.run

import kotlin.time.Duration

// For scheduling Worker from anywhere in app
// Implementation in => "run/data"
interface SyncRunScheduler {

    suspend fun scheduleSync(type: SyncType)

    // Cancel workers when user Logs out
    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data class FetchRuns(val interval: Duration) : SyncType

        data class DeleteRun(val runId: RunId) : SyncType

        class CreateRun(val run: Run, val mapPictureBytes: ByteArray) : SyncType
    }
}