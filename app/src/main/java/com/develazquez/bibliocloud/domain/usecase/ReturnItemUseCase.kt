package com.develazquez.bibliocloud.domain.usecase

import com.develazquez.bibliocloud.domain.model.EstadoPrestamo
import com.develazquez.bibliocloud.domain.model.Prestamo
import com.develazquez.bibliocloud.domain.repository.PrestamoRepository
import java.util.Date
import javax.inject.Inject

class ReturnItemUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(prestamoId: String): Result<Prestamo> {
        // Obtener el préstamo
        val prestamoResult = prestamoRepository.getPrestamoById(prestamoId)
        if (prestamoResult.isFailure) {
            return Result.failure(Exception("Préstamo no encontrado"))
        }

        val prestamo = prestamoResult.getOrNull()
        if (prestamo == null) {
            return Result.failure(Exception("Préstamo no encontrado"))
        }

        // Validar que el préstamo esté activo
        if (prestamo.estado != EstadoPrestamo.ACTIVO && prestamo.estado != EstadoPrestamo.ATRASADO) {
            return Result.failure(Exception("El préstamo ya ha sido devuelto"))
        }

        // Calcular si hay penalización
        val fechaActual = Date()
        val hayRetraso = fechaActual.after(prestamo.fechaFinPrevista)

        if (hayRetraso) {
            val diasRetraso = ((fechaActual.time - prestamo.fechaFinPrevista.time) / (1000 * 60 * 60 * 24)).toInt()
            // Aquí podrías calcular una penalización si es necesario
            // Por ahora solo registramos que hubo retraso
        }

        // Realizar la devolución
        return prestamoRepository.devolverPrestamo(prestamoId)
    }
}