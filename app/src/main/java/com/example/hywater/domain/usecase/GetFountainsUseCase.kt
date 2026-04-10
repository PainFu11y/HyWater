package com.example.hywater.domain.usecase

import com.example.hywater.domain.model.Fountain
import com.example.hywater.domain.repository.FountainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observes the fountain list from the local cache (via Flow) and triggers a
 * network refresh. The caller receives live updates every time the cache changes.
 *
 * Having a dedicated UseCase class makes business logic independently testable
 * and keeps ViewModels thin.
 */
class GetFountainsUseCase @Inject constructor(
    private val repository: FountainRepository
) {
    operator fun invoke(): Flow<List<Fountain>> = repository.observeFountains()
}
