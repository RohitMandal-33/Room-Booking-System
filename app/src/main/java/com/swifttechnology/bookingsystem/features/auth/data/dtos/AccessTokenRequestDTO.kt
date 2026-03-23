package com.swifttechnology.bookingsystem.features.auth.data.dtos

/**
 * Request body for logout and token refresh endpoints.
 * Used by POST /api/v1/logout and POST /api/v1/access/token.
 */
data class AccessTokenRequestDTO(
    val refreshToken: String
)
