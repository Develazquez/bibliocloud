package com.develazquez.bibliocloud.data.local.entity

import com.develazquez.bibliocloud.domain.model.*
import java.util.Date

// ==================== Recurso ====================

fun RecursoEntity.toDomain(): Recurso {
    return Recurso(
        id = id,
        titulo = titulo,
        categoria = when (categoria.uppercase()) {
            "LIBRO" -> CategoriaRecurso.LIBRO
            "HERRAMIENTA" -> CategoriaRecurso.HERRAMIENTA
            "DISPOSITIVO" -> CategoriaRecurso.DISPOSITIVO
            else -> CategoriaRecurso.OTRO
        },
        imagenUrl = imagenUrl,
        estado = when (estado.uppercase()) {
            "DISPONIBLE" -> EstadoRecurso.DISPONIBLE
            "PRESTADO" -> EstadoRecurso.PRESTADO
            "EN_MANTENIMIENTO" -> EstadoRecurso.EN_MANTENIMIENTO
            else -> EstadoRecurso.DISPONIBLE
        },
        descripcion = descripcion
    )
}

fun Recurso.toEntity(): RecursoEntity {
    return RecursoEntity(
        id = id,
        titulo = titulo,
        categoria = categoria.name,
        imagenUrl = imagenUrl,
        estado = estado.name,
        descripcion = descripcion
    )
}

// ==================== Prestamo ====================

fun PrestamoEntity.toDomain(): Prestamo {
    return Prestamo(
        id = id,
        usuarioId = usuarioId,
        recursoId = recursoId,
        fechaInicio = Date(fechaInicio),
        fechaFinPrevista = Date(fechaFinPrevista),
        fechaDevolucionReal = fechaDevolucionReal?.let { Date(it) },
        estado = when (estado.uppercase()) {
            "ACTIVO" -> EstadoPrestamo.ACTIVO
            "DEVUELTO" -> EstadoPrestamo.DEVUELTO
            "ATRASADO" -> EstadoPrestamo.ATRASADO
            else -> EstadoPrestamo.ACTIVO
        }
    )
}

fun Prestamo.toEntity(): PrestamoEntity {
    return PrestamoEntity(
        id = id,
        usuarioId = usuarioId,
        recursoId = recursoId,
        fechaInicio = fechaInicio.time,
        fechaFinPrevista = fechaFinPrevista.time,
        fechaDevolucionReal = fechaDevolucionReal?.time,
        estado = estado.name
    )
}
