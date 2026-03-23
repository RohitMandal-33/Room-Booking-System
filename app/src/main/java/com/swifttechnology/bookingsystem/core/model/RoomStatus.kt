package com.swifttechnology.bookingsystem.core.model

/**
 * Room status values.
 * ACTIVE / INACTIVE are the API values.
 * BOOKED / AVAILABLE / DISABLED are kept for legacy UI display purposes.
 */
enum class RoomStatus {
    ACTIVE, INACTIVE,
    BOOKED, AVAILABLE, DISABLED;

    companion object {
        fun fromApiString(value: String?): RoomStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: ACTIVE
    }
}
