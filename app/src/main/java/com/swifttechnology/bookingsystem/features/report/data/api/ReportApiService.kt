package com.swifttechnology.bookingsystem.features.report.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportDataRequestDTO
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportItemDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReportApiService {

    /** GET /api/v1/reports/get-all — fetch all reports (no filters) */
    @GET(APIEndpoint.REPORTS_GET_ALL)
    suspend fun getAllReports(): GlobalResponse<List<ReportItemDTO>>

    /** POST /api/v1/reports — fetch filtered / paginated reports */
    @POST(APIEndpoint.REPORTS_FILTERED)
    suspend fun getFilteredReports(
        @Body request: ReportDataRequestDTO
    ): GlobalResponse<List<ReportItemDTO>>
}
