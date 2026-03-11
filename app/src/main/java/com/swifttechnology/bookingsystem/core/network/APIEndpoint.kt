package com.swifttechnology.bookingsystem.core.network

import com.swifttechnology.bookingsystem.core.utils.Constants

object APIEndpoint {
    const val BASE_URL = Constants.BASE_URL

    // Auth
    const val LOGIN = "auth/login"
    const val LOGOUT = "auth/logout"
    const val REGISTER = "auth/register"
    const val REFRESH_TOKEN = "auth/refresh"

    // Dashboard
    const val DASHBOARD_STATS = "dashboard/stats"
    const val ANNOUNCEMENTS = "announcements"

    // Rooms
    const val ROOMS = "rooms"
    const val ROOM_DETAIL = "rooms/{id}"

    // Bookings
    const val BOOKINGS = "bookings"
    const val BOOKING_DETAIL = "bookings/{id}"
    const val MY_BOOKINGS = "bookings/mine"

    // Calendar
    const val CALENDAR_MEETINGS = "calendar/meetings"

    // Reports
    const val REPORTS = "reports"

    // Profile
    const val PROFILE = "profile"
    const val PROFILE_UPDATE = "profile/update"
}
