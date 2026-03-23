package com.swifttechnology.bookingsystem.features.booking.data.dtos

/**
 * Request body for POST /api/v1/book-room and PUT /api/v1/update-booked-room/{id}.
 */
data class RoomBookingRequestDTO(
    val meetingTitle: String? = null,
    val date: String? = null,              // yyyy-MM-dd
    val startTime: LocalTimeDTO? = null,
    val endTime: LocalTimeDTO? = null,
    val meetingType: String? = null,        // INTERNAL, CLIENT, EXECUTIVE
    val description: String? = null,
    val roomId: Long? = null,
    val internalParticipantIds: List<Long>? = null,
    val externalParticipants: List<ExternalParticipantDTO>? = null
)

/**
 * Time representation matching the API spec.
 */
data class LocalTimeDTO(
    val hour: Int,
    val minute: Int,
    val second: Int = 0,
    val nano: Int = 0
)

/**
 * External participant details.
 */
data class ExternalParticipantDTO(
    val name: String,
    val email: String
)

/**
 * Booking details returned from the API.
 */
data class BookingResponseDTO(
    val id: Long? = null,
    val meetingTitle: String? = null,
    val date: String? = null,
    val startTime: LocalTimeDTO? = null,
    val endTime: LocalTimeDTO? = null,
    val meetingType: String? = null,
    val description: String? = null,
    val roomId: Long? = null,
    val roomName: String? = null,
    val organizerEmail: String? = null,
    val internalParticipantIds: List<Long>? = null,
    val externalParticipants: List<ExternalParticipantDTO>? = null
)
