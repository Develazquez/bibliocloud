package com.develazquez.bibliocloud.domain.usecase

import com.develazquez.bibliocloud.domain.model.Prestamo
import com.develazquez.bibliocloud.domain.repository.PrestamoRepository
import javax.inject.Inject

class GetMyLoansUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(): Result<List<Prestamo>> {
        return prestamoRepository.getMisPrestamos()
    }
}