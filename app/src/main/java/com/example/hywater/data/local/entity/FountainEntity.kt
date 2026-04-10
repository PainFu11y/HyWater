package com.example.hywater.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for offline caching of fountains.
 * Mirrors the domain model but uses a flat schema suitable for SQLite.
 */
@Entity(tableName = "fountains")
data class FountainEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "photo_url") val photoUrl: String?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    /** Milliseconds since epoch — used to expire stale cache entries */
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis()
)
