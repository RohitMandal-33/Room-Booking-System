package com.swifttechnology.bookingsystem.features.report.data.repository

import com.swifttechnology.bookingsystem.features.report.data.api.ReportApiService
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportDataRequestDTO
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportItemDTO
import com.swifttechnology.bookingsystem.features.report.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val api: ReportApiService
) : ReportRepository {

    override suspend fun getAllReports(): Result<List<ReportItemDTO>> = runCatching {
        val response = api.getAllReports()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getFilteredReports(request: ReportDataRequestDTO): Result<List<ReportItemDTO>> = runCatching {
        val response = api.getFilteredReports(request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }
}
