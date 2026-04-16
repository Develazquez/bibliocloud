package com.develazquez.bibliocloud.features.loans.domain.entities

import java.util.Date
import com.develazquez.bibliocloud.features.catalog.domain.entities.Recurso
import com.develazquez.bibliocloud.features.auth.domain.entities.Usuario

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
    ATRASADO,
    VENCIDO
}
