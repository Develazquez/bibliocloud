package com.develazquez.bibliocloud.features.catalog.domain.usescases

import com.develazquez.bibliocloud.features.catalog.domain.entities.Book
import com.develazquez.bibliocloud.features.catalog.domain.entities.EstadoRecurso
import com.develazquez.bibliocloud.features.catalog.domain.repositories.RecursoRepository
import javax.inject.Inject

class GetAvailableBooksUseCase @Inject constructor(
    private val recursoRepository: RecursoRepository
) {
    suspend operator fun invoke(): Result<List<Book>> {
        return recursoRepository.getRecursosDisponibles().map { recursos ->
            recursos.map { recurso ->
                Book(
                    id = recurso.id,
                    title = recurso.titulo,
                    author = "Autor Desconocido", // Recurso no incluye autor actualmente
                    coverUrl = recurso.imagenUrl,
                    audioUrl = null, 
                    isAvailable = recurso.estado == EstadoRecurso.DISPONIBLE,
                    isLoanedByMe = false
                )
            }
        }
    }
}
