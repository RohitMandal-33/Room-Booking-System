package com.swifttechnology.bookingsystem.features.report.data.dtos

import com.google.gson.annotations.SerializedName

/**
 * POST /api/v1/reports  &  GET /api/v1/reports/export (query param)
 *
 * Matches the OpenAPI ReportDataRequest schema.
 */
data class ReportDataRequestDTO(
    val pageNo: Int? = null,
    val pageSize: Int? = null,
    val sortBy: String? = null,
    val sortDir: String? = null,
    val startDate: String? = null,   // yyyy-MM-dd
    val endDate: String? = null,     // yyyy-MM-dd
    val meetingType: String? = null, // INTERNAL | CLIENT | EXECUTIVE
    val roomName: String? = null,
    val createdBy: String? = null
)

/**
 * Single row returned inside the `data` array from
 *   GET  /api/v1/reports/get-all
 *   POST /api/v1/reports
 *
 * Field names match the JSON keys from the backend (note capital "E" in EndTime).
 */
data class ReportItemDTO(
    @SerializedName("meetingTitle") val meetingTitle: String? = null,
    @SerializedName("date")         val date: String? = null,
    @SerializedName("startTime")    val startTime: String? = null,
    @SerializedName("EndTime")      val endTime: String? = null,
    @SerializedName("roomName")     val roomName: String? = null,
    @SerializedName("createdBy")    val createdBy: String? = null
)
