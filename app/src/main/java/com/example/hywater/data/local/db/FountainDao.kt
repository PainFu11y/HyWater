package com.example.hywater.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hywater.data.local.entity.FountainEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FountainDao {

    /**
     * Observe all cached fountains as a Flow.
     * Room emits a new list automatically whenever the table changes,
     * making the UI reactive without polling.
     */
    @Query("SELECT * FROM fountains ORDER BY created_at DESC")
    fun observeAll(): Flow<List<FountainEntity>>

    @Query("SELECT * FROM fountains WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): FountainEntity?

    /**
     * Upsert a batch from the network response.
     * REPLACE strategy overwrites stale cached records with fresh data.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(fountains: List<FountainEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(fountain: FountainEntity)

    /** Remove entries older than [maxAgeMs] to keep the cache fresh. */
    @Query("DELETE FROM fountains WHERE cached_at < :maxAgeMs")
    suspend fun deleteStale(maxAgeMs: Long)

    @Query("DELETE FROM fountains")
    suspend fun deleteAll()
}
