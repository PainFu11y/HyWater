package com.example.hywater.data.mapper

import com.example.hywater.data.local.entity.FountainEntity
import com.example.hywater.data.remote.dto.FountainDto
import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.model.FountainStatus

// ── DTO → Domain ─────────────────────────────────────────────────────────────

fun FountainDto.toDomain(): Fountain = Fountain(
    id = id,
    latitude = latitude,
    longitude = longitude,
    description = description,
    photoUrl = photoUrl,
    status = status.toFountainStatus(),
    createdAt = createdAt
)

// ── DTO → Entity (for caching) ────────────────────────────────────────────────

fun FountainDto.toEntity(): FountainEntity = FountainEntity(
    id = id,
    latitude = latitude,
    longitude = longitude,
    description = description,
    photoUrl = photoUrl,
    status = status,
    createdAt = createdAt
)

// ── Entity → Domain ───────────────────────────────────────────────────────────

fun FountainEntity.toDomain(): Fountain = Fountain(
    id = id,
    latitude = latitude,
    longitude = longitude,
    description = description,
    photoUrl = photoUrl,
    status = status.toFountainStatus(),
    createdAt = createdAt
)

// ── Helper ────────────────────────────────────────────────────────────────────

private fun String.toFountainStatus(): FountainStatus =
    when (lowercase()) {
        "approved" -> FountainStatus.APPROVED
        else -> FountainStatus.PENDING
    }
