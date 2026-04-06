package com.develazquez.bibliocloud.features.auth.domain.entities

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val usuario: Usuario
)
