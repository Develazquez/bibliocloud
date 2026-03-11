package com.develazquez.bibliocloud.data.repository

import com.develazquez.bibliocloud.data.local.NetworkMonitor
import com.develazquez.bibliocloud.data.local.TokenManager
import com.develazquez.bibliocloud.data.local.dao.PrestamoDao
import com.develazquez.bibliocloud.data.local.entity.toDomain
import com.develazquez.bibliocloud.data.local.entity.toEntity
import com.develazquez.bibliocloud.data.remote.BiblioCloudApiService
import com.develazquez.bibliocloud.data.remote.dto.SolicitarPrestamoRequestDto
import com.develazquez.bibliocloud.data.remote.mapper.toDomain
import com.develazquez.bibliocloud.domain.model.Prestamo
import com.develazquez.bibliocloud.domain.repository.PrestamoRepository
import javax.inject.Inject

class PrestamoRepositoryImpl @Inject constructor(
    private val apiService: BiblioCloudApiService,
    private val tokenManager: TokenManager,
    private val prestamoDao: PrestamoDao,
    private val networkMonitor: NetworkMonitor
) : PrestamoRepository {

    private fun getAuthHeader(): String {
        return "Bearer ${tokenManager.getToken()}"
    }

    override suspend fun solicitarPrestamo(recursoId: String): Result<Prestamo> {
        return try {
            val usuarioId = tokenManager.getUserId()

            if (usuarioId == null) {
                return Result.failure(Exception("Sesión inválida. No se encontró ID de usuario. Por favor, re-inicia sesión."))
            }

            if (usuarioId.isEmpty()) {
                return Result.failure(Exception("Sesión inválida. ID de usuario vacío. Por favor, re-inicia sesión."))
            }

            if (usuarioId == "0") {
                return Result.failure(Exception("Error crítico: El servidor devolvió un ID de usuario inválido (0). Verifica con el administrador del servidor."))
            }

            if (recursoId == "0") {
                return Result.failure(Exception("Error: ID de recurso no válido."))
            }

            val dto = SolicitarPrestamoRequestDto(
                usuarioId = usuarioId.toInt(),
                recursoId = recursoId.toInt(),
                fechaInicio = obtenerFechaActual(),
                fechaLimite = calcularFechaLimite(),
                estado = "ACTIVO"
            )

            val response = apiService.solicitarPrestamo(getAuthHeader(), dto)

            if (response.isSuccessful && response.body() != null) {
                val prestamo = response.body()!!.toDomain()
                // Guardar en caché Room
                prestamoDao.insert(prestamo.toEntity())
                Result.success(prestamo)
            } else {
                Result.failure(Exception("No se pudo procesar el préstamo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    override suspend fun devolverPrestamo(prestamoId: String): Result<Prestamo> {
        return try {
            val response = apiService.devolverPrestamo(getAuthHeader(), prestamoId)

            if (response.isSuccessful && response.body() != null) {
                val prestamo = response.body()!!.toDomain()
                // Actualizar caché Room con el estado devuelto
                prestamoDao.insert(prestamo.toEntity())
                Result.success(prestamo)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Préstamo no encontrado"
                    400 -> "El préstamo ya fue devuelto"
                    else -> "Error al devolver préstamo: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }


    override suspend fun getMisPrestamos(): Result<List<Prestamo>> {
        return try {
            val usuarioId = tokenManager.getUserId()
            if (usuarioId.isNullOrEmpty() || usuarioId == "0") {
                return Result.failure(Exception("Error interno: ID de usuario no válido. Re-inicia sesión."))
            }

            if (networkMonitor.isConnected.value) {
                val response = apiService.getAllPrestamos(getAuthHeader())
                if (response.isSuccessful && response.body() != null) {
                    val todosPrestamos = response.body()!!.map { it.toDomain() }
                    val misPrestamos = todosPrestamos.filter {
                        it.usuarioId == usuarioId
                    }
                    // Persistir estados en caché Room
                    prestamoDao.deleteAll()
                    prestamoDao.insertAll(misPrestamos.map { it.toEntity() })
                    Result.success(misPrestamos)
                } else {
                    returnCachedPrestamos(usuarioId)
                }
            } else {
                // Sin red: retornar préstamos cacheados (persistencia de estados)
                returnCachedPrestamos(usuarioId)
            }
        } catch (e: Exception) {
            val usuarioId = tokenManager.getUserId()
            if (!usuarioId.isNullOrEmpty()) {
                val cached = prestamoDao.getByUsuarioId(usuarioId).map { it.toDomain() }
                if (cached.isNotEmpty()) {
                    return Result.success(cached)
                }
            }
            Result.failure(Exception("Sin conexión y sin datos locales"))
        }
    }

    override suspend fun getPrestamoById(id: String): Result<Prestamo> {
        return try {
            if (networkMonitor.isConnected.value) {
                val response = apiService.getPrestamoById(getAuthHeader(), id)
                if (response.isSuccessful && response.body() != null) {
                    val prestamo = response.body()!!.toDomain()
                    prestamoDao.insert(prestamo.toEntity())
                    Result.success(prestamo)
                } else {
                    returnCachedPrestamoById(id)
                }
            } else {
                returnCachedPrestamoById(id)
            }
        } catch (e: Exception) {
            returnCachedPrestamoById(id)
        }
    }

    // ========== Helpers ==========

    private suspend fun returnCachedPrestamos(usuarioId: String): Result<List<Prestamo>> {
        val cached = prestamoDao.getByUsuarioId(usuarioId).map { it.toDomain() }
        return if (cached.isNotEmpty()) {
            Result.success(cached)
        } else {
            Result.failure(Exception("No hay préstamos disponibles"))
        }
    }

    private suspend fun returnCachedPrestamoById(id: String): Result<Prestamo> {
        val cached = prestamoDao.getById(id)
        return if (cached != null) {
            Result.success(cached.toDomain())
        } else {
            Result.failure(Exception("Préstamo no encontrado"))
        }
    }

    private fun calcularFechaLimite(): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 7)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(calendar.time)
    }

    private fun obtenerFechaActual(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(java.util.Date())
    }
}