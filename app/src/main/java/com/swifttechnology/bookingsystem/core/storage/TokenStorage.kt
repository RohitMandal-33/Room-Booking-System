package com.swifttechnology.bookingsystem.core.storage

/**
 * Interface for token persistence operations.
 * Abstracting storage behind an interface lets you swap
 * DataStore, EncryptedSharedPreferences, or mocks in tests.
 * Mirrors iOS TokenStorage protocol.
 */
interface TokenStorage {
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clear()
    /** Performs a token refresh; return the new access token or null on failure. */
    suspend fun refreshToken(): String?
}
