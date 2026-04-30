package com.swifttechnology.bookingsystem.features.booking.data.dtos

import com.google.gson.annotations.SerializedName

/**
 * Request body for POST /api/v1/meeting-type and PUT /api/v1/update/{meetingTypeId}.
 */
data class MeetingTypeRequestDTO(
    @SerializedName("name") val name: String,
    @SerializedName("colorCode") val colorCode: String,
    @SerializedName("status") val status: String
)
