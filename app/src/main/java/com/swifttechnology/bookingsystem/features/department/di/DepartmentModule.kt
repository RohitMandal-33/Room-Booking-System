package com.swifttechnology.bookingsystem.features.department.di

import com.swifttechnology.bookingsystem.features.department.data.repository.DepartmentRepositoryImpl
import com.swifttechnology.bookingsystem.features.department.domain.repository.DepartmentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DepartmentModule {
    @Binds
    @Singleton
    abstract fun bindDepartmentRepository(impl: DepartmentRepositoryImpl): DepartmentRepository
}
