package com.swifttechnology.bookingsystem.shared.models

/**
 * Server-side pagination metadata.
 */
data class Pagination(
    val page: Int,
    val size: Int,
    val total: Int,
    val totalPages: Int
) {
    val hasNextPage: Boolean get() = page < totalPages
    val hasPreviousPage: Boolean get() = page > 1
}
