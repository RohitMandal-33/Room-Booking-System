package com.swifttechnology.bookingsystem.features.auth.domain.models

import com.swifttechnology.bookingsystem.core.auth.UserRole

/**
 * Domain user model — the canonical representation of a logged-in user.
 * Never expose DTO models (LoginResponseDTO) to the UI layer.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String? = null
)
