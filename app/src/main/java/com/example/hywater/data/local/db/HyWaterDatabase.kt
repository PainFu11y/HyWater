package com.example.hywater.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hywater.data.local.entity.FountainEntity

@Database(
    entities = [FountainEntity::class],
    version = 1,
    exportSchema = true
)
abstract class HyWaterDatabase : RoomDatabase() {

    abstract fun fountainDao(): FountainDao

    companion object {
        const val DATABASE_NAME = "hywater.db"
    }
}
