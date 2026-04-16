package com.develazquez.bibliocloud.features.admin.data.repositories

import com.develazquez.bibliocloud.core.network.TokenManager
import com.develazquez.bibliocloud.data.remote.dto.UsuarioDto
import com.develazquez.bibliocloud.data.remote.mapper.toDomain
import com.develazquez.bibliocloud.features.admin.data.remote.AdminApiService
import com.develazquez.bibliocloud.features.admin.domain.repositories.AdminRepository
import com.develazquez.bibliocloud.features.auth.domain.entities.Usuario
import com.develazquez.bibliocloud.features.loans.domain.entities.Prestamo
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val apiService: AdminApiService,
    private val tokenManager: TokenManager
) : AdminRepository {

    private fun getAuthToken(): String {
        val token = tokenManager.getToken()
        return if (token != null) "Bearer $token" else ""
    }

    override suspend fun getAllLoansForAdmin(): Result<List<Prestamo>> {
        return try {
            val response = apiService.getAllLoansForAdmin(getAuthToken())
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllUsersForAdmin(): Result<List<Usuario>> {
        return try {
            val response = apiService.getAllUsersForAdmin(getAuthToken())
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun updateUserRole(userId: String, newRole: String): Result<Usuario> {
        return try {
            val currentUserResponse = apiService.getAllUsersForAdmin(getAuthToken())
            val currentDto = currentUserResponse.body()?.firstOrNull { it.id.toString() == userId }
                ?: return Result.failure(Exception("Usuario no encontrado"))

            val updatedDto = currentDto.copy(rol = newRole)
            val response = apiService.updateUserRole(getAuthToken(), userId, updatedDto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserStatus(userId: String, newStatus: String): Result<Usuario> {
        return try {
            val currentUserResponse = apiService.getAllUsersForAdmin(getAuthToken())
            val currentDto = currentUserResponse.body()?.firstOrNull { it.id.toString() == userId }
                ?: return Result.failure(Exception("Usuario no encontrado"))

            val updatedDto = currentDto.copy(estado = newStatus)
            val response = apiService.updateUserStatus(getAuthToken(), userId, updatedDto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markLoanAsReturned(loanId: String): Result<Prestamo> {
        return try {
            val response = apiService.markLoanAsReturned(getAuthToken(), loanId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
