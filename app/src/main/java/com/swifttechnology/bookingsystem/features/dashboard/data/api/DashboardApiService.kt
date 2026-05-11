package com.swifttechnology.bookingsystem.features.dashboard.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.dashboard.data.dtos.DashboardResponseDTO
import retrofit2.http.GET

interface DashboardApiService {
    // gets the main landing page data stats, upcoming meetings, and announcements
    @GET(APIEndpoint.DASHBOARD)
    suspend fun getDashboardData(): GlobalResponse<DashboardResponseDTO>
}
