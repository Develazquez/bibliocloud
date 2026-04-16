package com.develazquez.bibliocloud.features.loans.domain.usescases

import com.develazquez.bibliocloud.features.catalog.domain.entities.EstadoRecurso
import com.develazquez.bibliocloud.features.auth.domain.entities.EstadoUsuario
import com.develazquez.bibliocloud.features.loans.domain.entities.Prestamo
import com.develazquez.bibliocloud.features.auth.domain.repositories.AuthRepository
import com.develazquez.bibliocloud.features.loans.domain.repositories.PrestamoRepository
import com.develazquez.bibliocloud.features.catalog.domain.repositories.RecursoRepository
import javax.inject.Inject

class BorrowItemUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository,
    private val recursoRepository: RecursoRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(recursoId: String): Result<Prestamo> {
        // Validar usuario actual
        val usuarioResult = authRepository.getCurrentUser()
        if (usuarioResult.isFailure) {
            return Result.failure(Exception("Usuario no autenticado"))
        }

        val usuario = usuarioResult.getOrNull()
        if (usuario == null) {
            return Result.failure(Exception("Usuario no encontrado"))
        }

        // Validar estado del usuario
        if (usuario.estado == EstadoUsuario.INACTIVO) {
            return Result.failure(Exception("Usuario en estado inactivo. No puede realizar préstamos"))
        }

        // Validar cantidad de préstamos
        if (usuario.cantidadPrestamosActuales >= 3) {
            return Result.failure(Exception("Límite de préstamos alcanzado (máximo 3)"))
        }

        // Validar disponibilidad del recurso
        val recursoResult = recursoRepository.getRecursoById(recursoId)
        if (recursoResult.isFailure) {
            return Result.failure(Exception("Recurso no encontrado"))
        }

        val recurso = recursoResult.getOrNull()
        if (recurso == null) {
            return Result.failure(Exception("Recurso no encontrado"))
        }

        if (recurso.estado != EstadoRecurso.DISPONIBLE) {
            return Result.failure(Exception("El recurso no está disponible en este momento"))
        }

        // Realizar la transacción
        return prestamoRepository.solicitarPrestamo(recursoId)
    }
}
