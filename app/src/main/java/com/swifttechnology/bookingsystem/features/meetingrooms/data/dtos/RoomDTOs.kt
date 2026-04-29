package com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos

/**
 * A single resource item returned from the server (e.g. {id:1, name:"PROJECTOR"}).
 * The API returns resources as objects, NOT strings.
 */
data class RoomResourceDTO(
    val id: Long? = null,
    val name: String? = null
)

data class RoomRequestDTO(
    val roomName: String,
    val capacity: Int,
    val resourcesIds: List<Long>? = null   // Matches OpenAPI RoomRequest.resourcesIds
)


data class PaginatedDataRequestDTO(
    val pageNo: Int? = 0,
    val pageSize: Int? = 10,
    val sortBy: String? = "id",
    val sortDir: String? = "asc"
)

data class RoomDataRequestDTO(
    val pageNo: Int? = 0,
    val pageSize: Int? = 10,
    val sortBy: String? = "id",
    val sortDir: String? = "asc",
    val roomName: String? = null
)


data class StatusChangeRequestDTO(
    val status: String
)

data class RoomResponseDTO(
    val id: Long,
    val roomName: String,
    val capacity: Int,
    val resources: List<Any>? = null,  // API is inconsistent: can be List<String> or List<Map<String, Any>>
    val status: String? = null
) {
    val resourceNames: List<String>
        get() = resources?.mapNotNull {
            when (it) {
                is String -> it
                is Map<*, *> -> it["name"] as? String
                else -> null
            }
        } ?: emptyList()

    val resourceIds: List<Long>
        get() = resources?.mapNotNull {
            when (it) {
                is Map<*, *> -> (it["id"] as? Number)?.toLong()
                else -> null
            }
        } ?: emptyList()
}

/**
 * Paginated room list response.
 */
data class RoomPageDTO(
    val content: List<RoomResponseDTO>? = null,
    val totalElements: Long? = null,
    val totalPages: Int? = null,
    val pageNo: Int? = null,
    val pageSize: Int? = null,
    val last: Boolean? = null
)

data class RoomResourceRequestDTO(
    val name: String
)
