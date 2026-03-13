package com.develazquez.bibliocloud.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recursos")
data class RecursoEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val categoria: String,
    val imagenUrl: String?,
    val estado: String,
    val descripcion: String?
)
