package com.example.hywater.domain.model

/**
 * Pure domain model — no Android or framework dependencies.
 * This type is what ViewModels and UseCases operate on.
 */
data class Fountain(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val photoUrl: String?,
    val status: FountainStatus,
    val createdAt: String
)

enum class FountainStatus {
    APPROVED,
    PENDING;

    val label: String
        get() = when (this) {
            APPROVED -> "Approved"
            PENDING -> "Pending Moderation"
        }
}
