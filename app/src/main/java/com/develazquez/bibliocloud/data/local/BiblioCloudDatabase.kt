package com.develazquez.bibliocloud.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.develazquez.bibliocloud.data.local.dao.PrestamoDao
import com.develazquez.bibliocloud.data.local.dao.RecursoDao
import com.develazquez.bibliocloud.data.local.entity.PrestamoEntity
import com.develazquez.bibliocloud.data.local.entity.RecursoEntity

@Database(
    entities = [RecursoEntity::class, PrestamoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BiblioCloudDatabase : RoomDatabase() {
    abstract fun recursoDao(): RecursoDao
    abstract fun prestamoDao(): PrestamoDao
}
