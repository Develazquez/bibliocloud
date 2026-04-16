package com.develazquez.bibliocloud.data.remote.mapper

import com.develazquez.bibliocloud.data.remote.dto.LoginResponseDto
import com.develazquez.bibliocloud.data.remote.dto.PrestamoDto
import com.develazquez.bibliocloud.data.remote.dto.RecursoDto
import com.develazquez.bibliocloud.data.remote.dto.UsuarioDto
import com.develazquez.bibliocloud.features.auth.domain.entities.*
import com.develazquez.bibliocloud.features.catalog.domain.entities.*
import com.develazquez.bibliocloud.features.loans.domain.entities.*
import java.text.SimpleDateFormat
import java.util.*

private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private val apiDateFormatWithFractions = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private fun parseDateSafe(dateString: String?): Date? {
    if (dateString.isNullOrEmpty()) return null
    return try {
        if (dateString.contains(".")) {
            val parts = dateString.split(".")
            val nanos = parts[1].removeSuffix("Z")
            if (nanos.length > 3) {
                // Truncate to milliseconds dynamically because SSSSSS is strict on digit count or might have fewer digits. Actually, SimpleDateFormat allows `.SSS` logic but let's just use the SSSSSS format.
                apiDateFormatWithFractions.parse(dateString)
            } else {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.parse(dateString)
            }
        } else {
            apiDateFormat.parse(dateString)
        }
    } catch (e: Exception) {
        null
    }
}

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        id = this.id.toString(),
        nombre = this.nombre ?: "Sin nombre",
        email = this.email ?: "",
        estado = when ((this.estado ?: "").uppercase()) {
            "ACTIVO" -> EstadoUsuario.ACTIVO
            "INACTIVO" -> EstadoUsuario.INACTIVO
            else -> EstadoUsuario.ACTIVO
        },
        cantidadPrestamosActuales = this.cantidadPrestamosActuales ?: 0,
        rol = when ((this.rol ?: "").uppercase()) {
            "ADMIN" -> RolUsuario.ADMIN
            else -> RolUsuario.USUARIO
        }
    )
}

fun RecursoDto.toDomain(): Recurso {
    return Recurso(
        id = this.id.toString(),
        titulo = this.titulo,
        categoria = when ((this.categoria ?: "").uppercase()) {
            "LIBRO" -> CategoriaRecurso.LIBRO
            "HERRAMIENTA" -> CategoriaRecurso.HERRAMIENTA
            "DISPOSITIVO" -> CategoriaRecurso.DISPOSITIVO
            else -> CategoriaRecurso.OTRO
        },
        imagenUrl = this.imagenUrl,
        estado = when ((this.estado ?: "").uppercase()) {
            "DISPONIBLE" -> EstadoRecurso.DISPONIBLE
            "PRESTADO" -> EstadoRecurso.PRESTADO
            "EN_MANTENIMIENTO" -> EstadoRecurso.EN_MANTENIMIENTO
            "NO_DISPONIBLE" -> EstadoRecurso.NO_DISPONIBLE
            else -> EstadoRecurso.DISPONIBLE
        },
        descripcion = this.descripcion,
        audioUrl = this.audioUrl,
        autor = this.autor,
        creadoPor = this.creadoPor
    )
}

fun PrestamoDto.toDomain(): Prestamo {
    return Prestamo(
        id = this.id.toString(),
        usuarioId = this.usuarioId.toString(),
        recursoId = this.recursoId.toString(),
        fechaInicio = parseDateSafe(this.fechaInicio) ?: Date(),
        fechaFinPrevista = parseDateSafe(this.fechaLimite) ?: Date(),
        fechaDevolucionReal = parseDateSafe(this.fechaDevolucion),
        estado = when ((this.estado ?: "").uppercase()) {
            "ACTIVO" -> EstadoPrestamo.ACTIVO
            "DEVUELTO" -> EstadoPrestamo.DEVUELTO
            "ATRASADO" -> EstadoPrestamo.ATRASADO
            "VENCIDO" -> EstadoPrestamo.VENCIDO
            else -> EstadoPrestamo.ACTIVO
        },
        recurso = this.recurso?.toDomain(),
        usuario = this.usuario?.toDomain()
    )
}

fun LoginResponseDto.toDomain(): LoginResponse {
    val usuario = this.usuario?.toDomain() ?: Usuario(
        id = "",
        nombre = "Usuario",
        email = "",
        estado = EstadoUsuario.ACTIVO,
        cantidadPrestamosActuales = 0
    )
    
    // Si el servidor no devuelve token, generar uno basado en el usuario
    val token = this.token ?: "token_${usuario.id}_${System.currentTimeMillis()}"
    
    return LoginResponse(
        token = token,
        usuario = usuario
    )
}
