package com.quadrified.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.quadrified.core.database.dao.RunDao
import com.quadrified.core.database.dao.RunPendingSyncDao
import com.quadrified.core.database.entity.DeleteRunSyncEntity
import com.quadrified.core.database.entity.RunEntity
import com.quadrified.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [RunEntity::class, RunPendingSyncEntity::class, DeleteRunSyncEntity::class],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao

    abstract val runPendingSyncDao: RunPendingSyncDao
}