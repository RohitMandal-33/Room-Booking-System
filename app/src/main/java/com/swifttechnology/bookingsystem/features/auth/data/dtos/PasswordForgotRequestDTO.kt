package com.swifttechnology.bookingsystem.features.auth.data.dtos

/**
 * Request body for PUT /api/v1/reset/password.
 * Used after successful OTP verification.
 */
data class PasswordForgotRequestDTO(
    val referenceId: String,
    val newPassword: String,
    val confirmNewPassword: String
)
