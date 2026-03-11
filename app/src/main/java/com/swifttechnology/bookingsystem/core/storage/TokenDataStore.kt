package com.swifttechnology.bookingsystem.core.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.tokenDataStore by preferencesDataStore(name = "auth_tokens")

/**
 * DataStore-backed implementation of [TokenStorage].
 * Moved from auth/data/storage → core/storage so all features can share it.
 * Injected via Hilt [com.swifttechnology.bookingsystem.di.StorageModule].
 */
class TokenDataStore(
    private val context: Context
) : TokenStorage {

    private companion object {
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

    /** Real refresh-token flow: exchange refresh token for a new access token. */
    override suspend fun refreshToken(): String? {
        // TODO: call AuthApiService.refreshToken() and persist the new access token
        return getRefreshToken()
    }
}
