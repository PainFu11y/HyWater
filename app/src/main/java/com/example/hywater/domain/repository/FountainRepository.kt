package com.example.hywater.domain.repository

import android.net.Uri
import com.example.hywater.domain.model.Fountain
import kotlinx.coroutines.flow.Flow

/**
 * Contract between the domain layer and the data layer.
 * The domain layer depends on this interface, never on the concrete implementation,
 * which keeps the domain framework-free and easily testable.
 */
interface FountainRepository {

    /** Live stream of cached fountains — emits whenever the cache changes. */
    fun observeFountains(): Flow<List<Fountain>>

    /** Pull latest approved fountains from the backend and refresh the cache. */
    suspend fun refreshFountains()

    /** Fetch a single fountain by ID (cache-first, then network). */
    suspend fun getFountainById(id: Long): Fountain

    /** Upload a new fountain photo + metadata. Returns the created fountain (status = PENDING). */
    suspend fun addFountain(
        photoUri: Uri,
        latitude: Double,
        longitude: Double,
        description: String
    ): Fountain
}
