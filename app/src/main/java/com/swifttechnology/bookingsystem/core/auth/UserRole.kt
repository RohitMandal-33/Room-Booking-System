package com.swifttechnology.bookingsystem.core.auth

/**
 * User roles in the booking system.
 * Maps to backend role strings for authorization checks.
 */
enum class UserRole(val key: String) {
    ADMIN("admin"),
    AUTHORIZED_PERSONNEL("authorized_personnel"),
    STAFF("staff");

    companion object {
        fun fromKey(key: String): UserRole =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: STAFF
    }
}
