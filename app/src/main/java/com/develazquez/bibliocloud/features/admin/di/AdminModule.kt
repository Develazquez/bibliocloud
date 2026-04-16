package com.develazquez.bibliocloud.features.admin.di

import com.develazquez.bibliocloud.core.network.TokenManager
import com.develazquez.bibliocloud.features.admin.data.remote.AdminApiService
import com.develazquez.bibliocloud.features.admin.data.repositories.AdminRepositoryImpl
import com.develazquez.bibliocloud.features.admin.domain.repositories.AdminRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminModule {

    @Provides
    @Singleton
    fun provideAdminApiService(retrofit: Retrofit): AdminApiService {
        return retrofit.create(AdminApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminRepository(
        apiService: AdminApiService,
        tokenManager: TokenManager
    ): AdminRepository {
        return AdminRepositoryImpl(apiService, tokenManager)
    }
}
