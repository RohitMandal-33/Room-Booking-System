package com.swifttechnology.bookingsystem.features.user.di

import com.swifttechnology.bookingsystem.features.user.data.repository.UserRepositoryImpl
import com.swifttechnology.bookingsystem.features.user.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
