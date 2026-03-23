package com.swifttechnology.bookingsystem.features.auth.data.dtos

/**
 * Request body for POST /api/v1/resend/otp.
 */
data class ResendOtpRequestDTO(
    val email: String,
    val otpPurpose: String = "FORGOT_PASSWORD"
)
