package com.develazquez.bibliocloud.core.di

import com.develazquez.bibliocloud.data.repository.AuthRepositoryImpl
import com.develazquez.bibliocloud.data.repository.PrestamoRepositoryImpl
import com.develazquez.bibliocloud.data.repository.RecursoRepositoryImpl
import com.develazquez.bibliocloud.domain.repository.AuthRepository
import com.develazquez.bibliocloud.domain.repository.PrestamoRepository
import com.develazquez.bibliocloud.domain.repository.RecursoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRecursoRepository(
        recursoRepositoryImpl: RecursoRepositoryImpl
    ): RecursoRepository

    @Binds
    @Singleton
    abstract fun bindPrestamoRepository(
        prestamoRepositoryImpl: PrestamoRepositoryImpl
    ): PrestamoRepository
}