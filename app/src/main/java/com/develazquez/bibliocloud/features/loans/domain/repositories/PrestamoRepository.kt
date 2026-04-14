package com.develazquez.bibliocloud.features.loans.domain.repositories

import com.develazquez.bibliocloud.features.loans.domain.entities.Prestamo

interface PrestamoRepository {
    suspend fun solicitarPrestamo(recursoId: String): Result<Prestamo>
    suspend fun devolverPrestamo(prestamoId: String): Result<Prestamo>
    suspend fun getMisPrestamos(): Result<List<Prestamo>>
    suspend fun getPrestamoById(id: String): Result<Prestamo>
}
