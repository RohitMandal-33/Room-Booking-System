package com.swifttechnology.bookingsystem.app.di

import com.swifttechnology.bookingsystem.core.network.NetworkProvider
import com.swifttechnology.bookingsystem.core.network.RefreshTokenApi
import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import com.swifttechnology.bookingsystem.features.auth.data.api.AuthApiService
import com.swifttechnology.bookingsystem.features.booking.data.api.BookingApiService
import com.swifttechnology.bookingsystem.features.department.data.api.DepartmentApiService
import com.swifttechnology.bookingsystem.features.meetingrooms.data.api.RoomApiService
import com.swifttechnology.bookingsystem.features.user.data.api.UserApiService
import dagger.Lazy
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
    fun provideOkHttpClient(tokenStorage: Lazy<TokenStorage>): OkHttpClient =
        NetworkProvider.buildOkHttpClient(tokenStorage)

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        NetworkProvider.buildRetrofit(okHttpClient)

    @Provides
    @Singleton
    fun provideRefreshTokenApi(retrofit: Retrofit): RefreshTokenApi =
        retrofit.create(RefreshTokenApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideRoomApiService(retrofit: Retrofit): RoomApiService =
        retrofit.create(RoomApiService::class.java)

    @Provides
    @Singleton
    fun provideBookingApiService(retrofit: Retrofit): BookingApiService =
        retrofit.create(BookingApiService::class.java)

    @Provides
    @Singleton
    fun provideDepartmentApiService(retrofit: Retrofit): DepartmentApiService =
        retrofit.create(DepartmentApiService::class.java)
}
