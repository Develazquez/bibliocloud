package com.example.bibliocloud.features.auth.presentation.register

data class RegisterState(
    val nombre: String = "",
    val telefono: String = "",
    val clave: String = "",
    val estaCargando: Boolean = false,
    val mensajeError: String? = null
)