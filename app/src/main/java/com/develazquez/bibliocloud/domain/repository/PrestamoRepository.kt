package com.develazquez.bibliocloud.domain.repository

import com.develazquez.bibliocloud.domain.model.Prestamo

interface PrestamoRepository {
    suspend fun solicitarPrestamo(recursoId: String): Result<Prestamo>
    suspend fun devolverPrestamo(prestamoId: String): Result<Prestamo>
    suspend fun getMisPrestamos(): Result<List<Prestamo>>
    suspend fun getPrestamoById(id: String): Result<Prestamo>
}