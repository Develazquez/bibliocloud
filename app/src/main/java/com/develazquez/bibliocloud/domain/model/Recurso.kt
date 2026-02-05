package com.develazquez.bibliocloud.domain.model

data class Recurso(
    val id: String,
    val titulo: String,
    val categoria: CategoriaRecurso,
    val imagenUrl: String?,
    val estado: EstadoRecurso,
    val descripcion: String?
)

enum class CategoriaRecurso {
    LIBRO,
    HERRAMIENTA,
    DISPOSITIVO,
    OTRO
}

enum class EstadoRecurso {
    DISPONIBLE,
    PRESTADO,
    EN_MANTENIMIENTO
}