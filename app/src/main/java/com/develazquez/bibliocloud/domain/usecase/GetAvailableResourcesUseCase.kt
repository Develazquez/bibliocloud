package com.develazquez.bibliocloud.domain.usecase

import com.develazquez.bibliocloud.domain.model.Recurso
import com.develazquez.bibliocloud.domain.repository.RecursoRepository
import javax.inject.Inject

class GetAvailableResourcesUseCase @Inject constructor(
    private val recursoRepository: RecursoRepository
) {
    suspend operator fun invoke(): Result<List<Recurso>> {
        return recursoRepository.getRecursosDisponibles()
    }
}