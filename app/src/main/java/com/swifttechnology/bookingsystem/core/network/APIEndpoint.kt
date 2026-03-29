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

    // Department
    const val DEPARTMENT_ADD = "api/v1/department/add"
}
