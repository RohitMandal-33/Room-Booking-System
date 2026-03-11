package com.swifttechnology.bookingsystem.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Wifi

val defaultAmenities = listOf(
    RoomAmenity("Projector", Icons.Outlined.Slideshow),
    RoomAmenity("Whiteboard", Icons.Outlined.BorderColor),
    RoomAmenity("Video", Icons.Outlined.Videocam),
    RoomAmenity("WiFi", Icons.Outlined.Wifi)
)

val defaultRooms = listOf(
    Room(
        "Conference Room A",
        RoomStatus.BOOKED,
        10,
        "Next Open Slot : 02:00 PM",
        defaultAmenities
    ),
    Room(
        "Board Room",
        RoomStatus.AVAILABLE,
        10,
        "Next Booking : 02:00 PM",
        defaultAmenities
    ),
    Room(
        "Training Room",
        RoomStatus.BOOKED,
        10,
        "Next Open Slot : 04:00 PM",
        defaultAmenities
    ),
    Room(
        "Focus Room 1",
        RoomStatus.DISABLED,
        10,
        "",
        defaultAmenities
    )
)

