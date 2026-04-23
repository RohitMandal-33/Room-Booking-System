package com.swifttechnology.bookingsystem.features.booking.data.dtos

/**
 * Request body for POST /api/v1/book-room and PUT /api/v1/update-booked-room/{id}.
 */
data class RoomBookingRequestDTO(
    val meetingTitle: String? = null,
    val date: String? = null,              // yyyy-MM-dd
    val startTime: String? = null,         // HH:mm:ss
    val endTime: String? = null,           // HH:mm:ss
    val meetingTypeId: Long? = null,
    val description: String? = null,
    val roomId: Long? = null,
    val internalParticipantIds: List<Long>? = null,
    val externalParticipants: List<ExternalParticipantDTO>? = null,
    val recurrenceEndDate: String? = null,
    val recurrenceType: String? = null,
    val weekDays: List<String>? = null,
    val dates: List<String>? = null
)

/**
 * Request body for Calendar APIs.
 */
data class CalenderRequestDTO(
    val date: String // yyyy-MM-dd
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
    val startTime: Any? = null, // Can be String ("HH:mm:ss") or LocalTimeDTO object
    val endTime: Any? = null,   // Can be String ("HH:mm:ss") or LocalTimeDTO object
    @com.google.gson.annotations.SerializedName("meetingType")
    val meetingTypeObj: Any? = null,
    val description: String? = null,
    val status: String? = null,
    val meetingStatus: String? = null,
    val meetingId: Long? = null,
    val roomId: Long? = null,
    val roomName: String? = null,
    val room: RoomDTO? = null,
    val roomBooker: RoomBookerDTO? = null,
    val internalParticipant: List<InternalParticipantDTO>? = null,
    val externalParticipant: List<ExternalParticipantDTO>? = null
) {
    val meetingType: String?
        get() = when (meetingTypeObj) {
            is String -> meetingTypeObj
            is Map<*, *> -> meetingTypeObj["name"] as? String
            else -> null
        }

    val meetingTypeColorCode: String?
        get() = when (meetingTypeObj) {
            is Map<*, *> -> meetingTypeObj["colorCode"] as? String
            else -> null
        }
    
    /**
     * Safely parses startTime as a String for UI consumption or further parsing.
     */
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

    /**
     * Safely parses endTime as a String for UI consumption or further parsing.
     */
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
