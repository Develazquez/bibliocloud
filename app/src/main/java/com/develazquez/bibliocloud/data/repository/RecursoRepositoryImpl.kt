package com.develazquez.bibliocloud.data.repository

import com.develazquez.bibliocloud.data.local.NetworkMonitor
import com.develazquez.bibliocloud.data.local.TokenManager
import com.develazquez.bibliocloud.data.local.dao.RecursoDao
import com.develazquez.bibliocloud.data.local.entity.toDomain
import com.develazquez.bibliocloud.data.local.entity.toEntity
import com.develazquez.bibliocloud.data.remote.BiblioCloudApiService
import com.develazquez.bibliocloud.data.remote.mapper.toDomain
import com.develazquez.bibliocloud.domain.model.CategoriaRecurso
import com.develazquez.bibliocloud.domain.model.EstadoRecurso
import com.develazquez.bibliocloud.domain.model.Recurso
import com.develazquez.bibliocloud.domain.repository.RecursoRepository
import javax.inject.Inject

class RecursoRepositoryImpl @Inject constructor(
    private val apiService: BiblioCloudApiService,
    private val tokenManager: TokenManager,
    private val recursoDao: RecursoDao,
    private val networkMonitor: NetworkMonitor
) : RecursoRepository {

    private fun getAuthHeader(): String {
        return "Bearer ${tokenManager.getToken()}"
    }

    /**
     * Estrategia cache-first:
     * 1. Intenta obtener datos frescos del API si hay red
     * 2. Si tiene éxito, guarda en caché Room y retorna datos frescos
     * 3. Si falla o no hay red, retorna datos cacheados localmente
     */
    override suspend fun getRecursos(): Result<List<Recurso>> {
        return try {
            if (networkMonitor.isConnected.value) {
                val response = apiService.getRecursos(getAuthHeader())
                if (response.isSuccessful && response.body() != null) {
                    val recursos = response.body()!!.map { it.toDomain() }
                    // Guardar en caché Room
                    recursoDao.deleteAll()
                    recursoDao.insertAll(recursos.map { it.toEntity() })
                    Result.success(recursos)
                } else {
                    // API falló, intentar caché
                    returnCachedRecursos()
                }
            } else {
                // Sin red, retornar caché
                returnCachedRecursos()
            }
        } catch (e: Exception) {
            // Error de red, retornar caché
            val cached = recursoDao.getAll().map { it.toDomain() }
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(Exception("Sin conexión y sin datos locales"))
            }
        }
    }

    override suspend fun getRecursosDisponibles(): Result<List<Recurso>> {
        return try {
            if (networkMonitor.isConnected.value) {
                val response = apiService.getRecursos(getAuthHeader())
                if (response.isSuccessful && response.body() != null) {
                    val todosLosRecursos = response.body()!!.map { it.toDomain() }
                    // Guardar todos en caché
                    recursoDao.deleteAll()
                    recursoDao.insertAll(todosLosRecursos.map { it.toEntity() })
                    val disponibles = todosLosRecursos.filter {
                        it.estado == EstadoRecurso.DISPONIBLE
                    }
                    Result.success(disponibles)
                } else {
                    returnCachedRecursosByEstado("DISPONIBLE")
                }
            } else {
                returnCachedRecursosByEstado("DISPONIBLE")
            }
        } catch (e: Exception) {
            val cached = recursoDao.getByEstado("DISPONIBLE").map { it.toDomain() }
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(Exception("Sin conexión y sin datos locales"))
            }
        }
    }

    override suspend fun getRecursoById(id: String): Result<Recurso> {
        return try {
            if (networkMonitor.isConnected.value) {
                val response = apiService.getRecursoById(getAuthHeader(), id)
                if (response.isSuccessful && response.body() != null) {
                    val recurso = response.body()!!.toDomain()
                    // Actualizar caché individual
                    recursoDao.insertAll(listOf(recurso.toEntity()))
                    Result.success(recurso)
                } else {
                    returnCachedRecursoById(id)
                }
            } else {
                returnCachedRecursoById(id)
            }
        } catch (e: Exception) {
            returnCachedRecursoById(id)
        }
    }


    override suspend fun buscarRecursos(query: String): Result<List<Recurso>> {
        return try {
            if (networkMonitor.isConnected.value) {
                try {
                    val response = apiService.getRecursos(getAuthHeader())
                    if (response.isSuccessful && response.body() != null) {
                        val recursos = response.body()!!.map { it.toDomain() }
                        recursoDao.deleteAll()
                        recursoDao.insertAll(recursos.map { it.toEntity() })
                    }
                } catch (_: Exception) {  }
            }
            val resultados = recursoDao.searchByQuery(query).map { it.toDomain() }
            Result.success(resultados)
        } catch (e: Exception) {
            Result.failure(Exception("Error al buscar: ${e.message}"))
        }
    }


    override suspend fun getRecursosPorCategoria(categoria: CategoriaRecurso): Result<List<Recurso>> {
        return try {
            if (networkMonitor.isConnected.value) {
                try {
                    val response = apiService.getRecursos(getAuthHeader())
                    if (response.isSuccessful && response.body() != null) {
                        val recursos = response.body()!!.map { it.toDomain() }
                        recursoDao.deleteAll()
                        recursoDao.insertAll(recursos.map { it.toEntity() })
                    }
                } catch (_: Exception) { /* Continuar con caché existente */ }
            }
            val porCategoria = recursoDao.getByCategoria(categoria.name).map { it.toDomain() }
            Result.success(porCategoria)
        } catch (e: Exception) {
            Result.failure(Exception("Error al filtrar por categoría: ${e.message}"))
        }
    }


    private suspend fun returnCachedRecursos(): Result<List<Recurso>> {
        val cached = recursoDao.getAll().map { it.toDomain() }
        return if (cached.isNotEmpty()) {
            Result.success(cached)
        } else {
            Result.failure(Exception("No hay datos disponibles"))
        }
    }

    private suspend fun returnCachedRecursosByEstado(estado: String): Result<List<Recurso>> {
        val cached = recursoDao.getByEstado(estado).map { it.toDomain() }
        return if (cached.isNotEmpty()) {
            Result.success(cached)
        } else {
            Result.failure(Exception("No hay datos disponibles"))
        }
    }

    private suspend fun returnCachedRecursoById(id: String): Result<Recurso> {
        val cached = recursoDao.getById(id)
        return if (cached != null) {
            Result.success(cached.toDomain())
        } else {
            Result.failure(Exception("Recurso no encontrado"))
        }
    }
}