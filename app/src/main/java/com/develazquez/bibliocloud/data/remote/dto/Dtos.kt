package com.develazquez.bibliocloud.data.remote.mapper

import com.develazquez.bibliocloud.data.remote.dto.LoginResponseDto
import com.develazquez.bibliocloud.data.remote.dto.PrestamoDto
import com.develazquez.bibliocloud.data.remote.dto.RecursoDto
import com.develazquez.bibliocloud.data.remote.dto.UsuarioDto
import com.develazquez.bibliocloud.domain.model.*
import java.util.Date

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        estado = when (this.estado.uppercase()) {
            "ACTIVO" -> EstadoUsuario.ACTIVO
            "DEUDOR" -> EstadoUsuario.DEUDOR
            else -> EstadoUsuario.ACTIVO
        },
        cantidadPrestamosActuales = this.cantidadPrestamosActuales
    )
}

fun RecursoDto.toDomain(): Recurso {
    return Recurso(
        id = this.id,
        titulo = this.titulo,
        categoria = when (this.categoria.uppercase()) {
            "LIBRO" -> CategoriaRecurso.LIBRO
            "HERRAMIENTA" -> CategoriaRecurso.HERRAMIENTA
            "DISPOSITIVO" -> CategoriaRecurso.DISPOSITIVO
            else -> CategoriaRecurso.OTRO
        },
        imagenUrl = this.imagenUrl,
        estado = when (this.estado.uppercase()) {
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
        id = this.id,
        usuarioId = this.usuarioId,
        recursoId = this.recursoId,
        fechaInicio = Date(this.fechaInicio),
        fechaFinPrevista = Date(this.fechaFinPrevista),
        fechaDevolucionReal = this.fechaDevolucionReal?.let { Date(it) },
        estado = when (this.estado.uppercase()) {
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
    return LoginResponse(
        token = this.token,
        usuario = this.usuario.toDomain()
    )
}