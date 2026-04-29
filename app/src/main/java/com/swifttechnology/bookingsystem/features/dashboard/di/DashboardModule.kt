package com.swifttechnology.bookingsystem.features.dashboard.di

import com.swifttechnology.bookingsystem.features.dashboard.data.api.DashboardApiService
import com.swifttechnology.bookingsystem.features.dashboard.data.repository.DashboardRepositoryImpl
import com.swifttechnology.bookingsystem.features.dashboard.domain.repository.DashboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    @Singleton
    fun provideDashboardApiService(retrofit: Retrofit): DashboardApiService {
        return retrofit.create(DashboardApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(
        dashboardApiService: DashboardApiService
    ): DashboardRepository {
        return DashboardRepositoryImpl(dashboardApiService)
    }
}
