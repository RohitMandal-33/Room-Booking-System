package com.swifttechnology.bookingsystem.features.auth.data.dtos

/**
 * Request body for POST /api/v1/forgot/password.
 * Sends an OTP to the user's registered email.
 */
data class ForgotPasswordOtpRequestDTO(
    val email: String
)
