package com.develazquez.bibliocloud.core.database.entity

import com.develazquez.bibliocloud.features.auth.domain.entities.*
import com.develazquez.bibliocloud.features.catalog.domain.entities.*
import com.develazquez.bibliocloud.features.loans.domain.entities.*
import java.util.Date
import com.develazquez.bibliocloud.features.catalog.data.datasources.local.entity.RecursoEntity
import com.develazquez.bibliocloud.features.loans.data.datasources.local.entity.PrestamoEntity


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
        descripcion = descripcion,
        audioUrl = audioUrl
    )
}

fun Recurso.toEntity(): RecursoEntity {
    return RecursoEntity(
        id = id,
        titulo = titulo,
        categoria = categoria.name,
        imagenUrl = imagenUrl,
        estado = estado.name,
        descripcion = descripcion,
        audioUrl = audioUrl
    )
}


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
