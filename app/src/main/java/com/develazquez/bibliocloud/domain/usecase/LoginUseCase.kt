package com.develazquez.bibliocloud.domain.usecase

import com.develazquez.bibliocloud.domain.model.LoginRequest
import com.develazquez.bibliocloud.domain.model.LoginResponse
import com.develazquez.bibliocloud.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<LoginResponse> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email y contraseña son requeridos"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Email inválido"))
        }

        return authRepository.login(LoginRequest(email, password))
    }
}