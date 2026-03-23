package com.swifttechnology.bookingsystem.features.auth.data.dtos

/**
 * Request body for POST /api/v1/verify/otp.
 */
data class OtpVerifyRequestDTO(
    val email: String,
    val otp: String
)

/**
 * Response payload from POST /api/v1/verify/otp.
 * The referenceId is required for the subsequent reset password call.
 */
data class OtpVerifyResponseDTO(
    val referenceId: String
)
