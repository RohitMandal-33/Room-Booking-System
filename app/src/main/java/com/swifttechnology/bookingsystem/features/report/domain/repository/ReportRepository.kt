package com.swifttechnology.bookingsystem.features.report.domain.repository

import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportDataRequestDTO
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportItemDTO

interface ReportRepository {
    suspend fun getAllReports(): Result<List<ReportItemDTO>>
    suspend fun getFilteredReports(request: ReportDataRequestDTO): Result<List<ReportItemDTO>>
    suspend fun exportReports(request: ReportDataRequestDTO): Result<okhttp3.ResponseBody>
}
