package com.swifttechnology.bookingsystem.app.di

import com.swifttechnology.bookingsystem.core.network.NetworkProvider
import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import com.swifttechnology.bookingsystem.features.auth.data.api.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenStorage: TokenStorage): OkHttpClient =
        NetworkProvider.buildOkHttpClient(tokenStorage)

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        NetworkProvider.buildRetrofit(okHttpClient)

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)
}

