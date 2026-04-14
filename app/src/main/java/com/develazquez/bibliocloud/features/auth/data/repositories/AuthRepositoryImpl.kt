package com.develazquez.bibliocloud.features.auth.data.repositories

import com.develazquez.bibliocloud.core.network.TokenManager
import com.develazquez.bibliocloud.data.remote.BiblioCloudApiService
import com.develazquez.bibliocloud.data.remote.dto.LoginRequestDto
import com.develazquez.bibliocloud.data.remote.dto.RegisterRequestDto
import com.develazquez.bibliocloud.data.remote.dto.FcmTokenRequestDto
import com.develazquez.bibliocloud.data.remote.mapper.toDomain
import com.develazquez.bibliocloud.features.auth.domain.entities.LoginRequest
import com.develazquez.bibliocloud.features.auth.domain.entities.LoginResponse
import com.develazquez.bibliocloud.features.auth.domain.entities.Usuario
import com.develazquez.bibliocloud.features.auth.domain.repositories.AuthRepository
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

                tokenManager.getFcmToken()?.let { fcmToken ->
                    registerFcmToken(fcmToken)
                }

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

                tokenManager.getFcmToken()?.let { fcmToken ->
                    registerFcmToken(fcmToken)
                }
                
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
            val fcmToken = tokenManager.getFcmToken()
            if (fcmToken != null) {
                removeFcmToken(fcmToken)
            }
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

    override suspend fun registerFcmToken(token: String): Result<Unit> {
        return try {
            tokenManager.saveFcmToken(token) // Guardamos localmente sin importar autenticación
            val userId = tokenManager.getUserId()
            if (userId == null || userId == "0") return Result.failure(Exception("Usuario no autenticado"))

            val response = apiService.registerFcmToken(userId, FcmTokenRequestDto(token))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al registrar token FCM"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFcmToken(token: String): Result<Unit> {
        return try {
            val userId = tokenManager.getUserId()
            if (userId == null || userId == "0") return Result.failure(Exception("Usuario no autenticado"))

            val response = apiService.removeFcmToken(userId, FcmTokenRequestDto(token))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al remover token FCM"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
