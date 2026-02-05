package com.develazquez.bibliocloud.data.repository

import com.develazquez.bibliocloud.data.local.TokenManager
import com.develazquez.bibliocloud.data.remote.BiblioCloudApiService
import com.develazquez.bibliocloud.data.remote.dto.LoginRequestDto
import com.develazquez.bibliocloud.data.remote.dto.RegisterRequestDto
import com.develazquez.bibliocloud.data.remote.mapper.toDomain
import com.develazquez.bibliocloud.domain.model.LoginRequest
import com.develazquez.bibliocloud.domain.model.LoginResponse
import com.develazquez.bibliocloud.domain.model.Usuario
import com.develazquez.bibliocloud.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: BiblioCloudApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val dto = LoginRequestDto(request.email, request.password)
            val response = apiService.login(dto)

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!.toDomain()
                tokenManager.saveToken(loginResponse.token)
                tokenManager.saveUserId(loginResponse.usuario.id)
                Result.success(loginResponse)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Credenciales incorrectas"
                    404 -> "Usuario no encontrado"
                    else -> "Error al iniciar sesión: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun register(usuario: Usuario, password: String): Result<LoginResponse> {
        return try {
            val dto = RegisterRequestDto(usuario.nombre, usuario.email, password)
            val response = apiService.register(dto)

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!.toDomain()
                tokenManager.saveToken(loginResponse.token)
                tokenManager.saveUserId(loginResponse.usuario.id)
                Result.success(loginResponse)
            } else {
                val errorMessage = when (response.code()) {
                    409 -> "El email ya está registrado"
                    400 -> "Datos inválidos"
                    else -> "Error al registrar: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: ${e.message}"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            tokenManager.clearToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<Usuario?> {
        return try {
            val token = tokenManager.getToken()
            if (token == null) {
                return Result.success(null)
            }

            val response = apiService.getCurrentUser("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al obtener usuario actual"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}