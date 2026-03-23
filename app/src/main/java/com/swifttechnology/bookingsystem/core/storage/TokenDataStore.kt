package com.swifttechnology.bookingsystem.core.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.swifttechnology.bookingsystem.core.network.AccessTokenRequest
import com.swifttechnology.bookingsystem.core.network.RefreshTokenApi
import kotlinx.coroutines.flow.first

private val Context.tokenDataStore by preferencesDataStore(name = "auth_tokens")

/**
 * DataStore-backed implementation of [TokenStorage].
 * Shared across all features via Hilt.
 */
class TokenDataStore(
    private val context: Context,
    private val refreshTokenApi: RefreshTokenApi
) : TokenStorage {

    private companion object {
        const val TAG = "TokenDataStore"
        val ACCESS_TOKEN_KEY: Preferences.Key<String> = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY: Preferences.Key<String> = stringPreferencesKey("refresh_token")
    }

    override suspend fun saveAccessToken(token: String) {
        context.tokenDataStore.edit { it[ACCESS_TOKEN_KEY] = token }
    }

    override suspend fun saveRefreshToken(token: String) {
        context.tokenDataStore.edit { it[REFRESH_TOKEN_KEY] = token }
    }

    override suspend fun getAccessToken(): String? =
        context.tokenDataStore.data.first()[ACCESS_TOKEN_KEY]

    override suspend fun getRefreshToken(): String? =
        context.tokenDataStore.data.first()[REFRESH_TOKEN_KEY]

    override suspend fun clear() {
        context.tokenDataStore.edit { it.clear() }
    }

    /**
     * Exchanges the stored refresh token for a new access token via the API.
     * On success, persists and returns the new access token.
     * On failure (403 / invalid), clears all tokens and returns null (force re-login).
     */
    override suspend fun refreshToken(): String? {
        val storedRefreshToken = getRefreshToken()
        if (storedRefreshToken.isNullOrBlank()) {
            Log.w(TAG, "No refresh token available — cannot refresh.")
            return null
        }

        return try {
            val response = refreshTokenApi.refreshAccessToken(
                AccessTokenRequest(refreshToken = storedRefreshToken)
            )
            if (response.success && response.data != null) {
                val newAccessToken = response.data.accessToken
                saveAccessToken(newAccessToken)
                newAccessToken
            } else {
                Log.w(TAG, "Refresh failed: ${response.message}")
                clear()
                null
            }
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "Refresh token HTTP error: ${e.code()}")
            if (e.code() == 403) {
                // Refresh token expired — force logout
                clear()
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Refresh token error", e)
            null
        }
    }
}
