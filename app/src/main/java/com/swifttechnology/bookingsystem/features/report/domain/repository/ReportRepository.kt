package com.swifttechnology.bookingsystem.features.report.domain.repository

import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportDataRequestDTO
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportItemDTO
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportPageDTO

interface ReportRepository {
    suspend fun getAllReports(): Result<ReportPageDTO>
    suspend fun getFilteredReports(request: ReportDataRequestDTO): Result<ReportPageDTO>
    suspend fun exportReports(request: ReportDataRequestDTO): Result<okhttp3.ResponseBody>
}
