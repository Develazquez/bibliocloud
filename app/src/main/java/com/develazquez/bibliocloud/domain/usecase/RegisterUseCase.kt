package com.develazquez.bibliocloud.domain.usecase

import com.develazquez.bibliocloud.domain.model.EstadoUsuario
import com.develazquez.bibliocloud.domain.model.LoginResponse
import com.develazquez.bibliocloud.domain.model.Usuario
import com.develazquez.bibliocloud.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        nombre: String,
        email: String,
        password: String
    ): Result<LoginResponse> {
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Todos los campos son requeridos"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Email inválido"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        val usuario = Usuario(
            id = "",
            nombre = nombre,
            email = email,
            estado = EstadoUsuario.ACTIVO,
            cantidadPrestamosActuales = 0
        )

        return authRepository.register(usuario, password)
    }
}