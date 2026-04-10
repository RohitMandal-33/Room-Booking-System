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
    val externalParticipants: List<ExternalParticipantDTO>? = null,
    val recurrenceEndDate: String? = null,
    val recurrenceType: String? = null,
    val weekDays: List<String>? = null
)


/**
 * LocalTime representation as an object, matching Spring's default JSON format for LocalTime.
 */
data class LocalTimeDTO(
    val hour: Int? = null,
    val minute: Int? = null,
    val second: Int? = null,
    val nano: Int? = null
)

/**
 * External participant details.
 */
data class ExternalParticipantDTO(
    val name: String,
    val email: String
)

data class InternalParticipantDTO(
    val id: Long? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null
)

data class RoomDTO(
    val id: Long? = null,
    val roomName: String? = null,
    val capacity: Int? = null,
    val bookedStatus: String? = null,
    val status: String? = null,
    val resources: List<String>? = null
)

data class RoomBookerDTO(
    val id: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null
)

/**
 * Booking details returned from the API.
 */
data class BookingResponseDTO(
    val id: Long? = null,
    val meetingTitle: String? = null,
    val date: String? = null,
    val startTime: Any? = null, // Can be String or LocalTimeDTO
    val endTime: Any? = null,   // Can be String or LocalTimeDTO
    val meetingType: String? = null,
    val description: String? = null,
    val status: String? = null,
    val meetingStatus: String? = null,
    val room: RoomDTO? = null,
    val roomBooker: RoomBookerDTO? = null,
    val internalParticipant: List<InternalParticipantDTO>? = null,
    val externalParticipant: List<ExternalParticipantDTO>? = null
)
