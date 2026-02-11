package com.develazquez.bibliocloud.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("Nombre") val nombre: String,
    @SerializedName("Email") val email: String,
    @SerializedName("Estado") val estado: String?,
    @SerializedName("CantidadPrestamosActuales") val cantidadPrestamosActuales: Int
)

data class RecursoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("imagen_url") val imagenUrl: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("descripcion") val descripcion: String?
)

data class PrestamoDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("UsuarioID") val usuarioId: Int,
    @SerializedName("RecursoID") val recursoId: Int,
    @SerializedName("FechaInicio") val fechaInicio: String,
    @SerializedName("FechaLimite") val fechaLimite: String,
    @SerializedName("FechaDevolucion") val fechaDevolucion: String?,
    @SerializedName("Estado") val estado: String?,
    @SerializedName("Recurso") val recurso: RecursoDto?,
    @SerializedName("Usuario") val usuario: UsuarioDto?
)

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("usuario") val usuario: UsuarioDto? = null
)

data class RegisterRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SolicitarPrestamoRequestDto(
    @SerializedName("UsuarioID") val usuarioId: Int,
    @SerializedName("RecursoID") val recursoId: Int,
    @SerializedName("FechaInicio") val fechaInicio: String,
    @SerializedName("FechaLimite") val fechaLimite: String,
    @SerializedName("Estado") val estado: String = "ACTIVO"
)