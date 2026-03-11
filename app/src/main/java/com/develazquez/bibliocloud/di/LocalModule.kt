package com.develazquez.bibliocloud.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.develazquez.bibliocloud.data.local.BiblioCloudDatabase
import com.develazquez.bibliocloud.data.local.NetworkMonitor
import com.develazquez.bibliocloud.data.local.TokenManager
import com.develazquez.bibliocloud.data.local.dao.PrestamoDao
import com.develazquez.bibliocloud.data.local.dao.RecursoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

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
    fun provideDatabase(@ApplicationContext context: Context): BiblioCloudDatabase {
        return Room.databaseBuilder(
            context,
            BiblioCloudDatabase::class.java,
            "bibliocloud_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideRecursoDao(database: BiblioCloudDatabase): RecursoDao {
        return database.recursoDao()
    }

    @Provides
    fun providePrestamoDao(database: BiblioCloudDatabase): PrestamoDao {
        return database.prestamoDao()
    }


    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
}