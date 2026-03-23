package com.swifttechnology.bookingsystem.features.booking.di

import com.swifttechnology.bookingsystem.features.booking.data.repository.BookingRepositoryImpl
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookingModule {
    @Binds
    @Singleton
    abstract fun bindBookingRepository(impl: BookingRepositoryImpl): BookingRepository
}
