package com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents

import androidx.compose.ui.graphics.Color

// A time range expressed in minutes from midnight (0 – 1440).

data class TimeRange(
    val startMinutes: Int,
    val endMinutes: Int
) {
    val durationMinutes: Int get() = endMinutes - startMinutes

    fun overlaps(other: TimeRange): Boolean =
        startMinutes < other.endMinutes && endMinutes > other.startMinutes
}

data class DraggableEvent(
    val id: String = "picker_event",
    val timeRange: TimeRange = TimeRange(9 * 60, 10 * 60),   // 09:00 – 10:00
    val title: String = "New Meeting",
    val color: Color = Color(0xFF9B6DFF),
    val wasAdjusted: Boolean = false   // true briefly after auto-shift due to conflict
)

// Which part of the event block the user is currently interacting with.
enum class DragHandle { TOP, BODY, BOTTOM }
