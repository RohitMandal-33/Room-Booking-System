package com.swifttechnology.bookingsystem.core.auth

enum class UserRole(val key: String) {
    ADMIN("admin"),
    AUTHORIZED_PERSONNEL("authorized_personnel"),
    STAFF("staff");

    companion object {
        fun fromKey(key: String): UserRole =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: STAFF
    }
}
