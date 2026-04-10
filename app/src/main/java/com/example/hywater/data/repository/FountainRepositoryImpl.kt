package com.example.hywater.data.repository

import android.net.Uri
import com.example.hywater.data.local.db.FountainDao
import com.example.hywater.data.mapper.toDomain
import com.example.hywater.data.mapper.toEntity
import com.example.hywater.data.remote.api.FountainApiService
import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.repository.FountainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Single source of truth: Room cache is observed by the UI.
 * Network calls refresh the cache; they never push directly to the UI.
 *
 * This offline-first pattern ensures the app remains usable without connectivity.
 */
class FountainRepositoryImpl @Inject constructor(
    private val api: FountainApiService,
    private val dao: FountainDao
) : FountainRepository {

    override fun observeFountains(): Flow<List<Fountain>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshFountains() {
        val response = api.getFountains()
        // Prune cache entries older than 24 hours before inserting fresh data
        dao.deleteStale(System.currentTimeMillis() - CACHE_EXPIRY_MS)
        dao.upsertAll(response.data.map { it.toEntity() })
    }

    override suspend fun getFountainById(id: Long): Fountain {
        // Try cache first; fall back to network
        val cached = dao.getById(id)
        if (cached != null) return cached.toDomain()
        val dto = api.getFountainById(id)
        dao.upsert(dto.toEntity())
        return dto.toDomain()
    }

    override suspend fun addFountain(
        photoUri: Uri,
        latitude: Double,
        longitude: Double,
        description: String
    ): Fountain {
        val file = File(photoUri.path!!)

        val photoPart = MultipartBody.Part.createFormData(
            name = "photo",
            filename = file.name,
            body = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        val latBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lngBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

        val dto = api.createFountain(photoPart, latBody, lngBody, descBody)

        // Cache the newly created fountain immediately so the user can see it locally
        dao.upsert(dto.toEntity())
        return dto.toDomain()
    }

    companion object {
        private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L // 24 h
    }
}
