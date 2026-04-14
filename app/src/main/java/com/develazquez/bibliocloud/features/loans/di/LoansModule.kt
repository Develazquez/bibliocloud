package com.develazquez.bibliocloud.features.loans.di

import com.develazquez.bibliocloud.features.loans.data.repositories.PrestamoRepositoryImpl
import com.develazquez.bibliocloud.features.loans.domain.repositories.PrestamoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoansModule {

    @Binds
    @Singleton
    abstract fun bindPrestamoRepository(
        prestamoRepositoryImpl: PrestamoRepositoryImpl
    ): PrestamoRepository
}
