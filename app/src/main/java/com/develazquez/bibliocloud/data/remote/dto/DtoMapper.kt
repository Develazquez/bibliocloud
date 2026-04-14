package com.develazquez.bibliocloud.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("cantidadPrestamosActuales") val cantidadPrestamosActuales: Int?
)

data class RecursoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("imagen_url") val imagenUrl: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("audio_url") val audioUrl: String?
)

data class PrestamoDto(
    @SerializedName("ID", alternate = ["id"]) val id: Int,
    @SerializedName("UsuarioID", alternate = ["usuarioId", "usuario_id"]) val usuarioId: Int,
    @SerializedName("RecursoID", alternate = ["recursoId", "recurso_id"]) val recursoId: Int,
    @SerializedName("FechaInicio", alternate = ["fechaInicio", "fecha_prestamo"]) val fechaInicio: String,
    @SerializedName("FechaLimite", alternate = ["fechaLimite", "fecha_devolucion_estimada"]) val fechaLimite: String,
    @SerializedName("FechaDevolucion", alternate = ["fechaDevolucion", "fecha_devolucion_real"]) val fechaDevolucion: String?,
    @SerializedName("Estado", alternate = ["estado"]) val estado: String?,
    @SerializedName("Recurso", alternate = ["recurso"]) val recurso: RecursoDto?,
    @SerializedName("Usuario", alternate = ["usuario"]) val usuario: UsuarioDto?
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
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("recurso_id") val recursoId: Int,
    @SerializedName("fecha_prestamo") val fechaInicio: String,
    @SerializedName("fecha_devolucion_estimada") val fechaLimite: String,
    @SerializedName("estado") val estado: String = "ACTIVO"
)

data class FcmTokenRequestDto(
    @SerializedName("token") val token: String
)