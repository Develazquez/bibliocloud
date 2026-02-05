package com.develazquez.bibliocloud.domain.usecase

import com.develazquez.bibliocloud.domain.model.EstadoRecurso
import com.develazquez.bibliocloud.domain.model.EstadoUsuario
import com.develazquez.bibliocloud.domain.model.Prestamo
import com.develazquez.bibliocloud.domain.repository.AuthRepository
import com.develazquez.bibliocloud.domain.repository.PrestamoRepository
import com.develazquez.bibliocloud.domain.repository.RecursoRepository
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
        if (usuario.estado == EstadoUsuario.DEUDOR) {
            return Result.failure(Exception("Usuario en estado deudor. Debe devolver préstamos atrasados"))
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