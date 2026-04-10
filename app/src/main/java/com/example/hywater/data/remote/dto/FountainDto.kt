package com.example.hywater.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FountainDto(
    @SerializedName("id") val id: Long,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("description") val description: String,
    @SerializedName("photo_url") val photoUrl: String?,
    @SerializedName("status") val status: String, // "approved" | "pending"
    @SerializedName("created_at") val createdAt: String
)

data class FountainsResponse(
    @SerializedName("data") val data: List<FountainDto>,
    @SerializedName("total") val total: Int
)
