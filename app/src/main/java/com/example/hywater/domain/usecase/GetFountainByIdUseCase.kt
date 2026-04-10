package com.example.hywater.domain.usecase

import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.repository.FountainRepository
import javax.inject.Inject

class GetFountainByIdUseCase @Inject constructor(
    private val repository: FountainRepository
) {
    suspend operator fun invoke(id: Long): Fountain = repository.getFountainById(id)
}
