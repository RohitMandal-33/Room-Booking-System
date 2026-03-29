package com.swifttechnology.bookingsystem.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Wifi

val defaultAmenities = listOf(
    RoomAmenity("WiFi", Icons.Outlined.Wifi),
    RoomAmenity("TV", Icons.Outlined.Videocam),
    RoomAmenity("Projector", Icons.Outlined.Slideshow),
    RoomAmenity("Whiteboard", Icons.Outlined.BorderColor)
)

val defaultRooms = listOf(
    Room(
        id = 1,
        name = "Grand Ballroom",
        status = RoomStatus.AVAILABLE,
        capacity = 50,
        timeLabel = "09:00 AM - 10:00 AM",
        amenities = defaultAmenities.take(3)
    ),
    Room(
        id = 2,
        name = "Meeting Room A",
        status = RoomStatus.BOOKED,
        capacity = 10,
        timeLabel = "11:00 AM - 12:00 PM",
        amenities = defaultAmenities.take(2)
    ),
    Room(
        id = 3,
        name = "Conference Room 1",
        status = RoomStatus.AVAILABLE,
        capacity = 25,
        timeLabel = "02:00 PM - 03:00 PM",
        amenities = defaultAmenities
    ),
    Room(
        id = 4,
        name = "Executive Suite",
        status = RoomStatus.DISABLED,
        capacity = 5,
        timeLabel = "",
        amenities = listOf(defaultAmenities[0])
    )
)
