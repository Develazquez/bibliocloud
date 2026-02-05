package com.develazquez.bibliocloud.domain.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val usuario: Usuario
)