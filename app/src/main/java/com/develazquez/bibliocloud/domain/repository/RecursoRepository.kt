package com.develazquez.bibliocloud.domain.repository

import com.develazquez.bibliocloud.domain.model.CategoriaRecurso
import com.develazquez.bibliocloud.domain.model.Recurso

interface RecursoRepository {
    suspend fun getRecursos(): Result<List<Recurso>>
    suspend fun getRecursosDisponibles(): Result<List<Recurso>>
    suspend fun getRecursoById(id: String): Result<Recurso>
    suspend fun buscarRecursos(query: String): Result<List<Recurso>>
    suspend fun getRecursosPorCategoria(categoria: CategoriaRecurso): Result<List<Recurso>>
}