package com.develazquez.bibliocloud.features.auth.domain.repositories

import com.develazquez.bibliocloud.features.auth.domain.entities.LoginRequest
import com.develazquez.bibliocloud.features.auth.domain.entities.LoginResponse
import com.develazquez.bibliocloud.features.auth.domain.entities.Usuario

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(usuario: Usuario, password: String): Result<LoginResponse>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<Usuario?>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun registerFcmToken(token: String): Result<Unit>
    suspend fun removeFcmToken(token: String): Result<Unit>
}
