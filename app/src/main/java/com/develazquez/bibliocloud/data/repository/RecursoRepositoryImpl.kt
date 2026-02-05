package com.develazquez.bibliocloud.data.repository

import com.develazquez.bibliocloud.data.local.TokenManager
import com.develazquez.bibliocloud.data.remote.BiblioCloudApiService
import com.develazquez.bibliocloud.data.remote.mapper.toDomain
import com.develazquez.bibliocloud.domain.model.CategoriaRecurso
import com.develazquez.bibliocloud.domain.model.Recurso
import com.develazquez.bibliocloud.domain.repository.RecursoRepository
import javax.inject.Inject

class RecursoRepositoryImpl @Inject constructor(
    private val apiService: BiblioCloudApiService,
    private val tokenManager: TokenManager
) : RecursoRepository {

    private fun getAuthHeader(): String {
        return "Bearer ${tokenManager.getToken()}"
    }

    override suspend fun getRecursos(): Result<List<Recurso>> {
        return try {
            val response = apiService.getRecursos(getAuthHeader())

            if (response.isSuccessful && response.body() != null) {
                val recursos = response.body()!!.map { it.toDomain() }
                Result.success(recursos)
            } else {
                Result.failure(Exception("Error al obtener recursos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun getRecursosDisponibles(): Result<List<Recurso>> {
        return try {
            val response = apiService.getRecursosDisponibles(getAuthHeader())

            if (response.isSuccessful && response.body() != null) {
                val recursos = response.body()!!.map { it.toDomain() }
                Result.success(recursos)
            } else {
                Result.failure(Exception("Error al obtener recursos disponibles"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun getRecursoById(id: String): Result<Recurso> {
        return try {
            val response = apiService.getRecursoById(getAuthHeader(), id)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Recurso no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun buscarRecursos(query: String): Result<List<Recurso>> {
        return try {
            val response = apiService.buscarRecursos(getAuthHeader(), query)

            if (response.isSuccessful && response.body() != null) {
                val recursos = response.body()!!.map { it.toDomain() }
                Result.success(recursos)
            } else {
                Result.failure(Exception("Error al buscar recursos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun getRecursosPorCategoria(categoria: CategoriaRecurso): Result<List<Recurso>> {
        return try {
            val categoriaStr = categoria.name
            val response = apiService.getRecursosPorCategoria(getAuthHeader(), categoriaStr)

            if (response.isSuccessful && response.body() != null) {
                val recursos = response.body()!!.map { it.toDomain() }
                Result.success(recursos)
            } else {
                Result.failure(Exception("Error al obtener recursos por categoría"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }
}