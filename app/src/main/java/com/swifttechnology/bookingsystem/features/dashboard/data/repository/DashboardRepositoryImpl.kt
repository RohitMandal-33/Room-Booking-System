package com.swifttechnology.bookingsystem.features.dashboard.data.repository

import com.swifttechnology.bookingsystem.features.dashboard.data.api.DashboardApiService
import com.swifttechnology.bookingsystem.features.dashboard.data.dtos.DashboardResponseDTO
import com.swifttechnology.bookingsystem.features.dashboard.domain.repository.DashboardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val apiService: DashboardApiService
) : DashboardRepository {
    override suspend fun getDashboardStats(): Result<DashboardResponseDTO> {
        return try {
            val response = apiService.getDashboardData()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch dashboard data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
