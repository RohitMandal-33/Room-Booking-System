package com.swifttechnology.bookingsystem.features.auth.data.dtos

/**
 * Request body for PUT /api/v1/change/password.
 * Requires an authenticated user (Bearer token).
 */
data class UpdatePasswordRequestDTO(
    val oldPassword: String,
    val newPassword: String,
    val confirmNewPassword: String
)
