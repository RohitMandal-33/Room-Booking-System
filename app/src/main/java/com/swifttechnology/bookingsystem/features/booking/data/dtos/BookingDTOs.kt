package com.swifttechnology.bookingsystem.features.booking.data.dtos

/**
 * Request body for POST /api/v1/book-room and PUT /api/v1/update-booked-room/{id}.
 */
data class RoomBookingRequestDTO(
    val meetingTitle: String? = null,
    val date: String? = null,              // yyyy-MM-dd
    val startTime: String? = null,
    val endTime: String? = null,
    val meetingType: String? = null,        // INTERNAL, CLIENT, EXECUTIVE
    val description: String? = null,
    val roomId: Long? = null,
    val internalParticipantIds: List<Long>? = null,
    val externalParticipants: List<ExternalParticipantDTO>? = null
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
    val startTime: String? = null,
    val endTime: String? = null,
    val meetingType: String? = null,
    val description: String? = null,
    val roomId: Long? = null,
    val roomName: String? = null,
    val organizerEmail: String? = null,
    val internalParticipantIds: List<Long>? = null,
    val externalParticipants: List<ExternalParticipantDTO>? = null
)
