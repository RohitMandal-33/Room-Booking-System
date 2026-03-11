package com.swifttechnology.bookingsystem.core.model

data class Room(
    val name: String,
    val status: RoomStatus,
    val capacity: Int,
    val timeLabel: String,
    val amenities: List<RoomAmenity>
)

