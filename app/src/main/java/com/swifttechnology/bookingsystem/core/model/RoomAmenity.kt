package com.swifttechnology.bookingsystem.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

data class RoomAmenity(val label: String, val icon: ImageVector) {
    companion object {
        fun fromResourceString(res: String): RoomAmenity? = when (res.uppercase()) {
            "WIFI" -> RoomAmenity("WiFi", Icons.Outlined.Wifi)
            "TV" -> RoomAmenity("TV", Icons.Outlined.Videocam)
            "PROJECTOR" -> RoomAmenity("Projector", Icons.Outlined.Slideshow)
            "WHITEBOARD" -> RoomAmenity("Whiteboard", Icons.Outlined.BorderColor)
            else -> null
        }
    }
}

