package com.develazquez.bibliocloud.data.repository

import com.develazquez.bibliocloud.data.local.TokenManager
import com.develazquez.bibliocloud.data.remote.BiblioCloudApiService
import com.develazquez.bibliocloud.data.remote.dto.SolicitarPrestamoRequestDto
import com.develazquez.bibliocloud.data.remote.mapper.toDomain
import com.develazquez.bibliocloud.domain.model.Prestamo
import com.develazquez.bibliocloud.domain.repository.PrestamoRepository
import javax.inject.Inject
class PrestamoRepositoryImpl @Inject constructor(
    private val apiService: BiblioCloudApiService,
    private val tokenManager: TokenManager
) : PrestamoRepository {

    private fun getAuthHeader(): String {
        return "Bearer ${tokenManager.getToken()}"
    }

// Archivo: com.develazquez.bibliocloud.data.repository.PrestamoRepositoryImpl.kt

    override suspend fun solicitarPrestamo(recursoId: String): Result<Prestamo> {
        return try {
            val usuarioId = tokenManager.getUserId()

            // Validación de seguridad contra el "ID 0"
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
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("No se pudo procesar el préstamo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    override suspend fun devolverPrestamo(prestamoId: String): Result<Prestamo> {
        return try {
            // POST /prestamos/{id}/devolver según tu README
            val response = apiService.devolverPrestamo(getAuthHeader(), prestamoId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
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

            val response = apiService.getAllPrestamos(getAuthHeader())

            if (response.isSuccessful && response.body() != null) {
                val todosPrestamos = response.body()!!.map { it.toDomain() }
                val misPrestamos = todosPrestamos.filter {
                    it.usuarioId == usuarioId
                }
                Result.success(misPrestamos)
            } else {
                Result.failure(Exception("Error al obtener préstamos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun getPrestamoById(id: String): Result<Prestamo> {
        return try {
            val response = apiService.getPrestamoById(getAuthHeader(), id)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Préstamo no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
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