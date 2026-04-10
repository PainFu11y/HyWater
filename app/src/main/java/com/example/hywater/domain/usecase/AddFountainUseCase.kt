package com.example.hywater.domain.usecase

import android.net.Uri
import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.repository.FountainRepository
import javax.inject.Inject

class AddFountainUseCase @Inject constructor(
    private val repository: FountainRepository
) {
    suspend operator fun invoke(
        photoUri: Uri,
        latitude: Double,
        longitude: Double,
        description: String
    ): Fountain = repository.addFountain(
        photoUri = photoUri,
        latitude = latitude,
        longitude = longitude,
        description = description
    )
}
