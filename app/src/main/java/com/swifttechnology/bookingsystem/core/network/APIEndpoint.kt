package com.swifttechnology.bookingsystem.core.network

import com.swifttechnology.bookingsystem.BuildConfig

object APIEndpoint {
    const val BASE_URL = BuildConfig.BASE_URL

    // Auth (public — no Bearer token required)
    const val LOGIN = "api/v1/login"
    const val LOGOUT = "api/v1/logout"
    const val REFRESH_TOKEN = "api/v1/access/token"
    const val FORGOT_PASSWORD = "api/v1/forgot/password"
    const val VERIFY_OTP = "api/v1/verify/otp"
    const val RESET_PASSWORD = "api/v1/reset/password"
    const val CHANGE_PASSWORD = "api/v1/change/password"
    const val RESEND_OTP = "api/v1/resend/otp"

    // User Management
    const val SIGNUP = "api/v1/signup"
    const val USERS_GET_ALL = "api/v1/users/get-all"
    const val USER_BY_ID = "api/v1/users/{id}"
    const val USER_GET_ALL_SIMPLE = "api/v1/user/get-all"
    const val USER_SEARCH = "api/v1/user/search"
    const val USER_UPDATE = "api/v1/user/{id}/update"

    // Meeting Rooms
    const val ROOM_ADD = "api/v1/room/add"
    const val ROOM_LIST = "api/v1/room/list"
    const val ROOM_ACTIVE_LIST = "api/v1/room/active-list"
    const val ROOM_UPDATE = "api/v1/room/{id}/update"
    const val ROOM_CHANGE_STATUS = "api/v1/room/{id}/change-status"

    // Room Bookings
    const val BOOK_ROOM = "api/v1/book-room"
    const val UPDATE_BOOKING = "api/v1/update-booked-room/{bookingId}"
    const val MY_BOOKINGS = "api/v1/get-booked-room"
    const val ALL_BOOKINGS = "api/v1/get-all-booked-room"
    const val UPCOMING_MEETING = "api/v1/upcoming-meeting"

    // Department
    const val DEPARTMENT_ADD = "api/v1/department/add"
    const val DEPARTMENT_LIST = "api/v1/department/list"

    // Custom Group
    const val GROUP_ADD = "api/v1/group/add"
    const val GROUP_LIST = "api/v1/group/list"
    const val GROUP_UPDATE = "api/v1/group/{id}/update"
    const val GROUP_DELETE = "api/v1/group/{id}/delete"

    // Reports
    const val REPORTS_GET_ALL = "api/v1/reports/get-all"
    const val REPORTS_FILTERED = "api/v1/reports"
    const val REPORTS_EXPORT   = "api/v1/reports/export"

    // Announcements
    const val ANNOUNCEMENT_LIST            = "api/v1/announcement/list"
    const val ANNOUNCEMENT_PINNED_LIST     = "api/v1/announcement/pinned-list"
    const val ANNOUNCEMENT_TARGETED        = "api/v1/get-Targeted-Audience-Announcement"
    const val ANNOUNCEMENT_TARGETED_PINNED = "api/v1/get-Targeted-Audience-pinned-Announcement"
    const val ANNOUNCEMENT_ADD             = "api/v1/announcement/add"
    const val ANNOUNCEMENT_UPDATE          = "api/v1/announcement/{id}/update"
    const val ANNOUNCEMENT_DELETE          = "api/v1/announcement/{id}/delete"
    const val ANNOUNCEMENT_CHANGE_PIN      = "api/v1/announcement/{id}/change-pin-status"
}
