package com.develazquez.bibliocloud.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.develazquez.bibliocloud.features.loans.data.datasources.local.dao.PrestamoDao
import com.develazquez.bibliocloud.features.catalog.data.datasources.local.dao.RecursoDao
import com.develazquez.bibliocloud.features.loans.data.datasources.local.entity.PrestamoEntity
import com.develazquez.bibliocloud.features.catalog.data.datasources.local.entity.RecursoEntity

@Database(
    entities = [RecursoEntity::class, PrestamoEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recursoDao(): RecursoDao
    abstract fun prestamoDao(): PrestamoDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE recursos ADD COLUMN autor TEXT")
                db.execSQL("ALTER TABLE recursos ADD COLUMN creadoPor INTEGER")
            }
        }
    }
}
