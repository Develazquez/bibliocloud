package com.develazquez.bibliocloud.features.catalog.di

import com.develazquez.bibliocloud.features.catalog.data.datasources.CatalogApiService
import com.develazquez.bibliocloud.features.catalog.data.repositories.RecursoRepositoryImpl
import com.develazquez.bibliocloud.features.catalog.domain.repositories.RecursoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {

    @Binds
    @Singleton
    abstract fun bindRecursoRepository(
        recursoRepositoryImpl: RecursoRepositoryImpl
    ): RecursoRepository

    companion object {
        @Provides
        @Singleton
        fun provideCatalogApiService(retrofit: Retrofit): CatalogApiService {
            return retrofit.create(CatalogApiService::class.java)
        }
    }
}
