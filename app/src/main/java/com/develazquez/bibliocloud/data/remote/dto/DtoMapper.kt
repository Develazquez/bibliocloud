package com.develazquez.bibliocloud.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("cantidad_prestamos_actuales") val cantidadPrestamosActuales: Int
)

data class RecursoDto(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("imagen_url") val imagenUrl: String?,
    @SerializedName("estado") val estado: String,
    @SerializedName("descripcion") val descripcion: String?
)

data class PrestamoDto(
    @SerializedName("id") val id: String,
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("recurso_id") val recursoId: String,
    @SerializedName("fecha_inicio") val fechaInicio: Long,
    @SerializedName("fecha_fin_prevista") val fechaFinPrevista: Long,
    @SerializedName("fecha_devolucion_real") val fechaDevolucionReal: Long?,
    @SerializedName("estado") val estado: String,
    @SerializedName("recurso") val recurso: RecursoDto?,
    @SerializedName("usuario") val usuario: UsuarioDto?
)

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("usuario") val usuario: UsuarioDto
)

data class RegisterRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class SolicitarPrestamoRequestDto(
    @SerializedName("recurso_id") val recursoId: String
)