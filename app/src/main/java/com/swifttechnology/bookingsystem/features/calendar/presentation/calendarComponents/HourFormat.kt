package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents

fun formatHourLabel(hour: Int): String =
    when (hour) {
        0 -> "12:00 am"
        in 1..11 -> "$hour:00 am"
        12 -> "12:00 pm"
        else -> "${hour - 12}:00 pm"
    }

