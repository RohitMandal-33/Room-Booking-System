package com.swifttechnology.bookingsystem.features.participants.di

import com.swifttechnology.bookingsystem.features.participants.data.repository.ParticipantRepositoryImpl
import com.swifttechnology.bookingsystem.features.participants.domain.repository.ParticipantRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ParticipantsModule {

    @Binds
    @Singleton
    abstract fun bindParticipantRepository(
        participantRepositoryImpl: ParticipantRepositoryImpl
    ): ParticipantRepository
}
