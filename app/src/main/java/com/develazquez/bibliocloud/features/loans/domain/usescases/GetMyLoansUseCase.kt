package com.develazquez.bibliocloud.features.loans.domain.usescases

import com.develazquez.bibliocloud.features.loans.domain.entities.Prestamo
import com.develazquez.bibliocloud.features.loans.domain.repositories.PrestamoRepository
import javax.inject.Inject

class GetMyLoansUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(): Result<List<Prestamo>> {
        return prestamoRepository.getMisPrestamos()
    }
}
