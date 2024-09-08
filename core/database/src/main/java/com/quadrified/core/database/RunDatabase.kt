package com.quadrified.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.quadrified.core.database.dao.RunDao
import com.quadrified.core.database.entity.RunEntity

@Database(
    entities = [RunEntity::class],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao
}