package com.develazquez.bibliocloud.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prestamos")
data class PrestamoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val recursoId: String,
    val fechaInicio: Long,
    val fechaFinPrevista: Long,
    val fechaDevolucionReal: Long?,
    val estado: String
)
