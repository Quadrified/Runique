package com.quadrified.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Run deleted locally but not deleted remotely
@Entity
data class DeleteRunSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String,
    val userId: String
)
