package com.develazquez.bibliocloud.domain.repository

import com.develazquez.bibliocloud.domain.model.LoginRequest
import com.develazquez.bibliocloud.domain.model.LoginResponse
import com.develazquez.bibliocloud.domain.model.Usuario

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(usuario: Usuario, password: String): Result<LoginResponse>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<Usuario?>
}