package com.swifttechnology.bookingsystem.features.meetingrooms.di

import com.swifttechnology.bookingsystem.features.meetingrooms.data.repository.RoomRepositoryImpl
import com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository.RoomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomModule {
    @Binds
    @Singleton
    abstract fun bindRoomRepository(impl: RoomRepositoryImpl): RoomRepository
}
