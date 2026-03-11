package com.swifttechnology.bookingsystem.app.di

import android.content.Context
import com.swifttechnology.bookingsystem.core.storage.TokenDataStore
import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage =
        TokenDataStore(context)
}

