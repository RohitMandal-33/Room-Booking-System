package com.swifttechnology.bookingsystem.features.dashboard.domain.repository

import com.swifttechnology.bookingsystem.features.dashboard.data.dtos.DashboardResponseDTO

interface DashboardRepository {
    suspend fun getDashboardStats(): Result<DashboardResponseDTO>
}
