package com.swifttechnology.bookingsystem.features.announcements.di

import com.swifttechnology.bookingsystem.features.announcements.data.repository.AnnouncementRepositoryImpl
import com.swifttechnology.bookingsystem.features.announcements.domain.repository.AnnouncementRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnnouncementModule {
    @Binds
    @Singleton
    abstract fun bindAnnouncementRepository(
        impl: AnnouncementRepositoryImpl
    ): AnnouncementRepository
}
