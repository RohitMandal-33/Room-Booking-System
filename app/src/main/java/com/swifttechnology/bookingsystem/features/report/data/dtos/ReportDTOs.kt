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
    val meetingTypeId: Long? = null, // Matches OpenAPI spec
    val roomName: String? = null,
    val createdBy: String? = null
)

/**
 * Paginated report list response.
 */
data class ReportPageDTO(
    val content: List<ReportItemDTO>? = null,
    val totalElements: Long? = null,
    val totalPages: Int? = null,
    val pageNo: Int? = null,
    val pageSize: Int? = null,
    val last: Boolean? = null
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
    @SerializedName("startTime")    val startTime: Any? = null, // Can be String or LocalTime object
    @SerializedName("endTime")      val endTime: Any? = null,   // Can be String or LocalTime object
    @SerializedName("roomName")     val roomName: String? = null,
    @SerializedName("createdBy")    val createdBy: String? = null
) {
    val startTimeString: String?
        get() = when (val time = startTime) {
            is String -> time
            is Map<*, *> -> {
                val h = (time["hour"] as? Number)?.toInt() ?: 0
                val m = (time["minute"] as? Number)?.toInt() ?: 0
                val s = (time["second"] as? Number)?.toInt() ?: 0
                String.format("%02d:%02d:%02d", h, m, s)
            }
            else -> null
        }

    val endTimeString: String?
        get() = when (val time = endTime) {
            is String -> time
            is Map<*, *> -> {
                val h = (time["hour"] as? Number)?.toInt() ?: 0
                val m = (time["minute"] as? Number)?.toInt() ?: 0
                val s = (time["second"] as? Number)?.toInt() ?: 0
                String.format("%02d:%02d:%02d", h, m, s)
            }
            else -> null
        }
}
