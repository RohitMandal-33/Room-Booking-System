package com.swifttechnology.bookingsystem.core.network

/**
 * Generic API response wrapper matching the server envelope:
 * { "success": true, "message": "...", "data": { ... } }
 */
data class APIResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)
