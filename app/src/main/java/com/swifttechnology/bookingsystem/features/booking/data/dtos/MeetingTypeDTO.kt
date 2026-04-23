package com.swifttechnology.bookingsystem.features.booking.data.dtos

import com.google.gson.annotations.SerializedName

/**
 * Meeting type as returned by GET /api/v1/get-all-meeting-type.
 * Contains id, display name, and a CSS/web colorCode string (e.g. "#4CD8A8" or "rgb(76,216,168)").
 */
data class MeetingTypeDTO(
    val id: Long? = null,
    val name: String? = null,
    val colorCode: String? = null,
    val status: String? = null
)
