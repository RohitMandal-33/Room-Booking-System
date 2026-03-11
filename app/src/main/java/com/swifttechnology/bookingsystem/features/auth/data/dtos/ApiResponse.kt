package com.swifttechnology.bookingsystem.features.auth.data.dtos

data class ApiResponse<T>(
    val data: T?,
    val success: Boolean,
    val message: String?
)

