package com.develazquez.bibliocloud.features.auth.domain.entities


data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val estado: EstadoUsuario,
    val cantidadPrestamosActuales: Int
)

enum class EstadoUsuario {
    ACTIVO,
    DEUDOR
}
