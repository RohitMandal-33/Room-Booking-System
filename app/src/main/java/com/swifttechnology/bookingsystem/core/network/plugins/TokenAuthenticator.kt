package com.swifttechnology.bookingsystem.core.network.plugins

import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * OkHttp Authenticator triggered on 401 responses.
 * Attempts a token refresh; if it fails or the token hasn't changed, the request is aborted
 * to avoid an infinite retry loop.
 */
class TokenAuthenticator(
    private val tokenStorage: TokenStorage
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loop: abort after 2 consecutive 401s
        if (responseCount(response) >= 2) return null

        val requestToken = response.request.header("Authorization")
            ?.removePrefix("Bearer ")

        // Attempt token refresh; currently returns stored refresh token as placeholder
        val newToken = runBlocking { tokenStorage.refreshToken() }

        // If token hasn't changed or refresh failed, do not retry
        return if (newToken.isNullOrBlank() || newToken == requestToken) {
            null
        } else {
            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
