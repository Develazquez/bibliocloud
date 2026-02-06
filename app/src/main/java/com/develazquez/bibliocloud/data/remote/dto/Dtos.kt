package com.develazquez.bibliocloud.data.remote.mapper

import com.develazquez.bibliocloud.data.remote.dto.LoginResponseDto
import com.develazquez.bibliocloud.data.remote.dto.PrestamoDto
import com.develazquez.bibliocloud.data.remote.dto.RecursoDto
import com.develazquez.bibliocloud.data.remote.dto.UsuarioDto
import com.develazquez.bibliocloud.domain.model.*
import java.util.Date

fun UsuarioDto.toDomain(): Usuario {
    return Usuario(
        // Si el id del DTO es null, le asignamos un String vacío ""
        // Esto evita el error "parameter id is null"
        id = this.id ?: "",
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
        id = this.id,
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
        id = this.id,
        usuarioId = this.usuarioId,
        recursoId = this.recursoId,
        fechaInicio = Date(this.fechaInicio),
        fechaFinPrevista = Date(this.fechaFinPrevista),
        fechaDevolucionReal = this.fechaDevolucionReal?.let { Date(it) },
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

// Localizado en com.develazquez.bibliocloud.data.remote.mapper

fun LoginResponseDto.toDomain(): LoginResponse {
    return LoginResponse(
        // Si 'token' es null en el DTO, le asignamos "" para que no truene el Dominio
        token = this.token ?: "",
        usuario = this.usuario?.toDomain() ?: Usuario(
            id = "",
            nombre = "Usuario",
            email = "",
            estado = EstadoUsuario.ACTIVO,
            cantidadPrestamosActuales = 0
        )
    )
}