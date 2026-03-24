package com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos

/**
 * Request body for adding or updating a meeting room.
 * resources values: WIFI, TV, PROJECTOR, WHITEBOARD
 */
data class RoomRequestDTO(
    val roomName: String,
    val capacity: Int,
    val resources: List<String>? = null
)


data class PaginatedDataRequestDTO(
    val pageNo: Int? = 0,
    val pageSize: Int? = 10,
    val sortBy: String? = "id",
    val sortDir: String? = "asc"
)


data class StatusChangeRequestDTO(
    val status: String
)

data class RoomResponseDTO(
    val id: Long,
    val roomName: String,
    val capacity: Int,
    val resources: List<String>? = null,
    val status: String? = null
)

/**
 * Paginated room list response.
 */
data class RoomPageDTO(
    val data: List<RoomResponseDTO>? = null,
    val totalElements: Long? = null,
    val totalPages: Int? = null,
    val pageNo: Int? = null,
    val pageSize: Int? = null,
    val last: Boolean? = null
)
