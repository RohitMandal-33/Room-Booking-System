package com.swifttechnology.bookingsystem.core.network.plugins

import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that attaches the Bearer token to every outgoing request,
 * except for public endpoints that do not require authentication.
 */
class TokenPlugin(
    private val tokenStorage: Lazy<TokenStorage>
) : Interceptor {

    companion object {
        /** Endpoints that must NOT carry an Authorization header. */
        private val PUBLIC_PATHS = listOf(
            "api/v1/login",
            "api/v1/logout",
            "api/v1/access/token",
            "api/v1/forgot/password",
            "api/v1/verify/otp",
            "api/v1/reset/password",
            "api/v1/resend/otp"
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding Authorization header for public endpoints
        val isPublic = PUBLIC_PATHS.any { originalRequest.url.encodedPath.contains(it) }
        if (isPublic) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { tokenStorage.get().getAccessToken() }

        val request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
