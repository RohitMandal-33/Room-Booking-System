package com.swifttechnology.bookingsystem.features.dashboard.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.dashboard.data.dtos.DashboardResponseDTO
import retrofit2.http.GET

interface DashboardApiService {
    @GET(APIEndpoint.DASHBOARD)
    suspend fun getDashboardData(): GlobalResponse<DashboardResponseDTO>
}
