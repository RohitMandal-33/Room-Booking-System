package com.swifttechnology.bookingsystem.core.network

/**
 * Sealed hierarchy representing all possible API/network errors.
 * Matches the iOS APIError pattern — consumers switch on type, not raw codes.
 */
sealed class APIError : Exception() {

    /** Device has no connectivity. */
    object NoConnection : APIError() {
        override val message = "No internet connection. Please check your network."
    }

    /** Server returned a non-2xx HTTP status. */
    data class HttpError(
        val code: Int,
        override val message: String = "HTTP error $code"
    ) : APIError()

    /** Response body could not be decoded into the expected model. */
    data class DecodingError(
        override val message: String = "Failed to decode server response."
    ) : APIError()

    /** Server returned success=false with a message. */
    data class ServerError(
        override val message: String
    ) : APIError()

    /** Catch-all for anything else. */
    data class UnknownError(
        override val message: String = "An unexpected error occurred."
    ) : APIError()
}
