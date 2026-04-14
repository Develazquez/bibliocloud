package com.develazquez.bibliocloud.features.catalog.domain.usescases

import com.develazquez.bibliocloud.features.catalog.domain.entities.Recurso
import com.develazquez.bibliocloud.features.catalog.domain.repositories.RecursoRepository
import javax.inject.Inject

class GetAvailableResourcesUseCase @Inject constructor(
    private val recursoRepository: RecursoRepository
) {
    suspend operator fun invoke(): Result<List<Recurso>> {
        return recursoRepository.getRecursosDisponibles()
    }
}
