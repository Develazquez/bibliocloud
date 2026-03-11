package com.develazquez.bibliocloud.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.develazquez.bibliocloud.data.local.entity.RecursoEntity

@Dao
interface RecursoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recursos: List<RecursoEntity>)

    @Query("SELECT * FROM recursos")
    suspend fun getAll(): List<RecursoEntity>

    @Query("SELECT * FROM recursos WHERE id = :id")
    suspend fun getById(id: String): RecursoEntity?

    @Query("SELECT * FROM recursos WHERE estado = :estado")
    suspend fun getByEstado(estado: String): List<RecursoEntity>

    @Query("SELECT * FROM recursos WHERE titulo LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%'")
    suspend fun searchByQuery(query: String): List<RecursoEntity>

    @Query("SELECT * FROM recursos WHERE categoria = :categoria")
    suspend fun getByCategoria(categoria: String): List<RecursoEntity>

    @Query("DELETE FROM recursos")
    suspend fun deleteAll()
}
