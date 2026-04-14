package com.develazquez.bibliocloud.features.catalog.domain.repositories

import com.develazquez.bibliocloud.features.catalog.domain.entities.CategoriaRecurso
import com.develazquez.bibliocloud.features.catalog.domain.entities.Recurso

interface RecursoRepository {
    suspend fun getRecursos(): Result<List<Recurso>>
    suspend fun getRecursosDisponibles(): Result<List<Recurso>>
    suspend fun getRecursoById(id: String): Result<Recurso>
    suspend fun buscarRecursos(query: String): Result<List<Recurso>>
    suspend fun getRecursosPorCategoria(categoria: CategoriaRecurso): Result<List<Recurso>>
}
