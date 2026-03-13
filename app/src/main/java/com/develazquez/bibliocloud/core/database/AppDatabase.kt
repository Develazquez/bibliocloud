package com.develazquez.bibliocloud.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.develazquez.bibliocloud.core.database.dao.PrestamoDao
import com.develazquez.bibliocloud.core.database.dao.RecursoDao
import com.develazquez.bibliocloud.core.database.entity.PrestamoEntity
import com.develazquez.bibliocloud.core.database.entity.RecursoEntity

@Database(
    entities = [RecursoEntity::class, PrestamoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recursoDao(): RecursoDao
    abstract fun prestamoDao(): PrestamoDao
}
