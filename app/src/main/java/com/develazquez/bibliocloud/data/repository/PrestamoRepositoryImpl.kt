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

    override suspend fun solicitarPrestamo(recursoId: String): Result<Prestamo> {
        return try {
            val dto = SolicitarPrestamoRequestDto(recursoId)
            val response = apiService.solicitarPrestamo(getAuthHeader(), dto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                val errorMessage = when (response.code()) {
                    409 -> "El recurso ya no está disponible"
                    403 -> "Has alcanzado el límite de préstamos"
                    else -> "Error al solicitar préstamo: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun devolverPrestamo(prestamoId: String): Result<Prestamo> {
        return try {
            val response = apiService.devolverPrestamo(getAuthHeader(), prestamoId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al devolver préstamo"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun getMisPrestamos(): Result<List<Prestamo>> {
        return try {
            val response = apiService.getMisPrestamos(getAuthHeader())

            if (response.isSuccessful && response.body() != null) {
                val prestamos = response.body()!!.map { it.toDomain() }
                Result.success(prestamos)
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
}