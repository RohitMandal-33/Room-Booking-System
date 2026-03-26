package com.swifttechnology.bookingsystem.features.auth.domain.models

import com.swifttechnology.bookingsystem.core.auth.UserRole

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String? = null
)
