package com.develazquez.bibliocloud.features.auth.domain.usescases

import com.develazquez.bibliocloud.features.auth.domain.entities.LoginRequest
import com.develazquez.bibliocloud.features.auth.domain.entities.LoginResponse
import com.develazquez.bibliocloud.features.auth.domain.repositories.AuthRepository
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
