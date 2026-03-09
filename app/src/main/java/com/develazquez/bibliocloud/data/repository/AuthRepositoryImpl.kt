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
                val body = response.body()!!
                
                if (body.usuario == null) {
                    return Result.failure(Exception("Error: Servidor no devolvió información del usuario"))
                }
                
                val loginResponse = body.toDomain()

                if (loginResponse.usuario.id.isEmpty() || loginResponse.usuario.id == "0") {
                    return Result.failure(Exception("Error: ID de usuario inválido recibido del servidor"))
                }

                tokenManager.saveToken(loginResponse.token)
                tokenManager.saveUserId(loginResponse.usuario.id)

                Result.success(loginResponse)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
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
                val body = response.body()!!
                
                // Validar que el usuario no sea nulo
                if (body.usuario == null) {
                    return Result.failure(Exception("Error: Servidor no devolvió información del usuario"))
                }
                
                val loginResponse = body.toDomain()
                
                // Validar que el ID del usuario sea válido antes de guardar
                if (loginResponse.usuario.id.isEmpty() || loginResponse.usuario.id == "0") {
                    return Result.failure(Exception("Error: ID de usuario inválido recibido del servidor"))
                }
                
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
            val userId = tokenManager.getUserId()

            if (token == null || userId == null) {
                return Result.success(null)
            }

            val response = apiService.getCurrentUser("Bearer $token", userId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al obtener usuario actual"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NUEVO: Implementación para borrar cuenta en el servidor Go y limpiar datos locales
    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val token = tokenManager.getToken()
            val userId = tokenManager.getUserId()

            if (token == null) {
                return Result.failure(Exception("No hay una sesión activa"))
            }

            val response = apiService.deleteUser("Bearer $token", userId)

            if (response.isSuccessful) {
                // Si el servidor Go borra el usuario con éxito, limpiamos el token local
                tokenManager.clearToken()
                Result.success(Unit)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Sesión expirada o no autorizada"
                    403 -> "No tienes permisos para realizar esta acción"
                    else -> "Error al eliminar cuenta: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al intentar eliminar cuenta: ${e.message}"))
        }
    }

}