package com.swifttechnology.bookingsystem.core.network.plugins

import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * OkHttp Authenticator triggered on 401 responses.
 * Attempts a token refresh; if it fails or the token hasn't changed, the request is aborted
 * to avoid an infinite retry loop.
 */
class TokenAuthenticator(
    private val tokenStorage: Lazy<TokenStorage>
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loop: abort after 2 consecutive 401s
        if (responseCount(response) >= 2) return null

        val requestToken = response.request.header("Authorization")
            ?.removePrefix("Bearer ")

        // Use a Mutex to ensure that only one thread refreshes the token at a time
        val newToken = runBlocking(kotlinx.coroutines.Dispatchers.IO) { 
            mutex.withLock {
                val currentToken = tokenStorage.get().getAccessToken()
                // If token was already refreshed by another thread waiting before us, reuse it
                if (currentToken != null && currentToken != requestToken) {
                    return@withLock currentToken
                }
                
                // Otherwise, proceed to call the refresh API
                tokenStorage.get().refreshToken() 
            }
        }

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
