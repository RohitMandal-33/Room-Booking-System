package com.swifttechnology.bookingsystem.core.model

data class Room(
    val id: Long = 0L,
    val name: String,
    val status: RoomStatus,
    val capacity: Int,
    val timeLabel: String = "",
    val amenities: List<RoomAmenity> = emptyList(),
    val resources: List<String> = emptyList(),      // Display names only (e.g. "Projector")
    val resourceIds: List<Long> = emptyList()        // Server IDs, used for PUT requests
)
