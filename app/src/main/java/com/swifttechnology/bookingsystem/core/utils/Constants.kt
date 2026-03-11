package com.swifttechnology.bookingsystem.core.utils

object Constants {
    // Api
    const val BASE_URL = "http://10.7.1.198:8081/"
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L

    // DataStore
    const val TOKEN_STORE_NAME = "auth_tokens"
    const val USER_PREFS_STORE_NAME = "user_defaults"

    // Date formats
    const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    const val DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val TIME_FORMAT_DISPLAY = "hh:mm a"
    const val DATE_TIME_DISPLAY = "MMM dd, yyyy hh:mm a"

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val DEFAULT_PAGE = 1

    // Booking statuses
    const val STATUS_PENDING = "pending"
    const val STATUS_CONFIRMED = "confirmed"
    const val STATUS_CANCELLED = "cancelled"
    const val STATUS_COMPLETED = "completed"

    // Misc
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_BOOKING_HOURS = 8
}
