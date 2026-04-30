package com.swifttechnology.bookingsystem.features.report.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportDataRequestDTO
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportItemDTO
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportPageDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReportApiService {

    /** GET /api/v1/reports/get-all — fetch all reports (no filters) */
    @GET(APIEndpoint.REPORTS_GET_ALL)
    suspend fun getAllReports(): GlobalResponse<ReportPageDTO>

    @POST(APIEndpoint.REPORTS_FILTERED)
    suspend fun getFilteredReports(
        @Body request: ReportDataRequestDTO
    ): GlobalResponse<ReportPageDTO>

    /** POST /api/v1/reports/export — download reports */
    @retrofit2.http.Streaming
    @POST(APIEndpoint.REPORTS_EXPORT)
    suspend fun exportReports(
        @Body request: ReportDataRequestDTO
    ): retrofit2.Response<okhttp3.ResponseBody>
}
