package com.develazquez.bibliocloud.data.remote.mapper

import com.develazquez.bibliocloud.data.remote.dto.LoginResponseDto
import com.develazquez.bibliocloud.data.remote.dto.PrestamoDto
import com.develazquez.bibliocloud.data.remote.dto.RecursoDto
import com.develazquez.bibliocloud.data.remote.dto.UsuarioDto
import com.develazquez.bibliocloud.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        id = this.id.toString(),
        nombre = this.nombre ?: "Sin nombre",
        email = this.email ?: "",
        estado = when ((this.estado ?: "").uppercase()) {
            "ACTIVO" -> EstadoUsuario.ACTIVO
            "DEUDOR" -> EstadoUsuario.DEUDOR
            else -> EstadoUsuario.ACTIVO
        },
        cantidadPrestamosActuales = this.cantidadPrestamosActuales ?: 0
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
            else -> EstadoRecurso.DISPONIBLE
        },
        descripcion = this.descripcion
    )
}

fun PrestamoDto.toDomain(): Prestamo {
    return Prestamo(
        id = this.id.toString(),
        usuarioId = this.usuarioId.toString(),
        recursoId = this.recursoId.toString(),
        fechaInicio = try { apiDateFormat.parse(this.fechaInicio) ?: Date() } catch (e: Exception) { Date() },
        fechaFinPrevista = try { apiDateFormat.parse(this.fechaLimite) ?: Date() } catch (e: Exception) { Date() },
        fechaDevolucionReal = this.fechaDevolucion?.let {
            try { apiDateFormat.parse(it) } catch (e: Exception) { null }
        },
        estado = when ((this.estado ?: "").uppercase()) {
            "ACTIVO" -> EstadoPrestamo.ACTIVO
            "DEVUELTO" -> EstadoPrestamo.DEVUELTO
            "ATRASADO" -> EstadoPrestamo.ATRASADO
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
