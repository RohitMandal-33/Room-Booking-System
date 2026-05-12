package com.swifttechnology.bookingsystem.core.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Centralized utility to map raw exceptions and server error strings into
 * user-friendly messages. Use [map] in repository catch/onFailure blocks and
 * [sanitizeServerMessage] when the server returns `success=false`.
 */
object ErrorMapper {

    /**
     * Maps any [Throwable] to a human-readable error message suitable for
     * display in the UI. Never exposes raw exception class names or stack traces.
     */
    fun map(error: Throwable): String = when (error) {

        // No connectivity
        is UnknownHostException ->
            "No internet connection. Please check your network."

        // Request timed out
        is SocketTimeoutException ->
            "The request timed out. Please try again."

        // General I/O (covers ConnectException, etc.)
        is IOException ->
            "A network error occurred. Please check your connection and try again."

        // HTTP status-code errors from Retrofit
        is HttpException -> mapHttpCode(error.code())

        // Server-returned business error wrapped as a plain Exception
        // (repositories do: throw Exception(response.message))
        else -> {
            val raw = error.message
            if (!raw.isNullOrBlank()) sanitizeServerMessage(raw)
            else "Something went wrong. Please try again."
        }
    }

    /**
     * Maps HTTP status codes to user-friendly strings.
     */
    private fun mapHttpCode(code: Int): String = when (code) {
        400 -> "Invalid request. Please check your input and try again."
        401 -> "Invalid credentials or session expired. Please try again."
        403 -> "You don't have permission to perform this action."
        404 -> "The requested resource was not found."
        408 -> "The request timed out. Please try again."
        409 -> "A conflict occurred. This record may already exist."
        422 -> "The provided data is invalid. Please review your input."
        429 -> "Too many requests. Please wait a moment and try again."
        in 500..599 -> "A server error occurred. Please try again later."
        else -> "Something went wrong (error $code). Please try again."
    }

    /**
     * Cleans up a raw server-returned message so it is safe and friendly
     * to display. Strips Java exception prefixes and excessively technical
     * detail while preserving the useful part of the message.
     */
    fun sanitizeServerMessage(raw: String): String {
        if (raw.isBlank()) return "Something went wrong. Please try again."

        // Remove common exception prefixes
        val cleaned = raw
            .replace(Regex("^(java\\.\\S+Exception:\\s*)+", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^(Exception:\\s*)+", RegexOption.IGNORE_CASE), "")
            .trim()

        // If after cleaning there's nothing useful, fall back
        if (cleaned.isBlank()) return "Something went wrong. Please try again."

        // Capitalise first letter and return
        return cleaned.replaceFirstChar { it.uppercaseChar() }
    }
}
