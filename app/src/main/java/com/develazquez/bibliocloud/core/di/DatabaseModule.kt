package com.develazquez.bibliocloud.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.develazquez.bibliocloud.core.database.AppDatabase
import com.develazquez.bibliocloud.core.hardware.NetworkMonitor
import com.develazquez.bibliocloud.core.network.TokenManager
import com.develazquez.bibliocloud.core.database.dao.PrestamoDao
import com.develazquez.bibliocloud.core.database.dao.RecursoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("bibliocloud_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenManager(sharedPreferences: SharedPreferences): TokenManager {
        return TokenManager(sharedPreferences)
    }



    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bibliocloud_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideRecursoDao(database: AppDatabase): RecursoDao {
        return database.recursoDao()
    }

    @Provides
    fun providePrestamoDao(database: AppDatabase): PrestamoDao {
        return database.prestamoDao()
    }


    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
}