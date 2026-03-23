package com.swifttechnology.bookingsystem.core.network

/**
 * Unified API response wrapper matching the server GlobalResponse envelope.
 * All API services should use this instead of feature-local response classes.
 */
data class GlobalResponse<T>(
    val data: T?,
    val success: Boolean,
    val message: String,
    val errorCode: String? = null
)
