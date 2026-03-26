package com.swifttechnology.bookingsystem.core.model

enum class RoomStatus {
    ACTIVE, INACTIVE,
    BOOKED, AVAILABLE, DISABLED;

    companion object {
        fun fromApiString(value: String?): RoomStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: ACTIVE
    }
}
