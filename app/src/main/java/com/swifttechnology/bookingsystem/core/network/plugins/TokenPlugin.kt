package com.swifttechnology.bookingsystem.core.network.plugins

import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that attaches the Bearer token to every outgoing request.
 * Mirrors iOS TokenPlugin — keeps auth header injection in one place.
 */
class TokenPlugin(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding Authorization header for the login endpoint.
        if (originalRequest.url.encodedPath.contains("api/v1/login")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { tokenStorage.getAccessToken() }

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
