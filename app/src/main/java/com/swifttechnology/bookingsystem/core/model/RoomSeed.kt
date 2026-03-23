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
        1L,
        "Conference Room A",
        RoomStatus.BOOKED,
        10,
        "Next Open Slot : 02:00 PM",
        defaultAmenities
    ),
    Room(
        2L,
        "Board Room",
        RoomStatus.AVAILABLE,
        8,
        "Next Booking : 02:00 PM",
        defaultAmenities
    ),
    Room(
        3L,
        "Training Room",
        RoomStatus.BOOKED,
        20,
        "Next Open Slot : 04:00 PM",
        defaultAmenities
    ),
    Room(
        4L,
        "Focus Room 1",
        RoomStatus.DISABLED,
        4,
        "",
        defaultAmenities
    ),
    Room(
        5L,
        "Focus Room 2",
        RoomStatus.AVAILABLE,
        4,
        "Next Booking : 03:30 PM",
        defaultAmenities
    ),
    Room(
        6L,
        "Meeting Room Alpha",
        RoomStatus.BOOKED,
        12,
        "Next Open Slot : 05:00 PM",
        defaultAmenities
    ),
    Room(
        7L,
        "Meeting Room Beta",
        RoomStatus.AVAILABLE,
        6,
        "Next Booking : 01:30 PM",
        defaultAmenities
    ),
    Room(
        8L,
        "Strategy Room",
        RoomStatus.AVAILABLE,
        10,
        "Next Booking : 04:15 PM",
        defaultAmenities
    ),
    Room(
        9L,
        "Design Studio",
        RoomStatus.BOOKED,
        15,
        "Next Open Slot : 06:00 PM",
        defaultAmenities
    ),
    Room(
        10L,
        "Innovation Lab",
        RoomStatus.DISABLED,
        12,
        "",
        defaultAmenities
    )
)
