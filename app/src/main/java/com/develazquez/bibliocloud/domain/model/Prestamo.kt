package com.develazquez.bibliocloud.domain.model

import java.util.Date

data class Prestamo(
    val id: String,
    val usuarioId: String,
    val recursoId: String,
    val fechaInicio: Date,
    val fechaFinPrevista: Date,
    val fechaDevolucionReal: Date?,
    val estado: EstadoPrestamo,
    val recurso: Recurso? = null,
    val usuario: Usuario? = null
)

enum class EstadoPrestamo {
    ACTIVO,
    DEVUELTO,
    ATRASADO
}