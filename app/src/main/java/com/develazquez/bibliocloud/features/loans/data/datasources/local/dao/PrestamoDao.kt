package com.develazquez.bibliocloud.features.loans.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.develazquez.bibliocloud.features.loans.data.datasources.local.entity.PrestamoEntity

@Dao
interface PrestamoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prestamos: List<PrestamoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prestamo: PrestamoEntity)

    @Query("SELECT * FROM prestamos WHERE usuarioId = :usuarioId")
    suspend fun getByUsuarioId(usuarioId: String): List<PrestamoEntity>

    @Query("SELECT * FROM prestamos WHERE id = :id")
    suspend fun getById(id: String): PrestamoEntity?

    @Query("SELECT * FROM prestamos WHERE usuarioId = :usuarioId AND estado = :estado")
    suspend fun getByUsuarioIdAndEstado(usuarioId: String, estado: String): List<PrestamoEntity>

    @Query("DELETE FROM prestamos")
    suspend fun deleteAll()
}

