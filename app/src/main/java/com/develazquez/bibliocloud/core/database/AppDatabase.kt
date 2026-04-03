package com.develazquez.bibliocloud.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.develazquez.bibliocloud.features.loans.data.datasources.local.dao.PrestamoDao
import com.develazquez.bibliocloud.features.catalog.data.datasources.local.dao.RecursoDao
import com.develazquez.bibliocloud.features.loans.data.datasources.local.entity.PrestamoEntity
import com.develazquez.bibliocloud.features.catalog.data.datasources.local.entity.RecursoEntity

@Database(
    entities = [RecursoEntity::class, PrestamoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recursoDao(): RecursoDao
    abstract fun prestamoDao(): PrestamoDao
}
