package com.swifttechnology.bookingsystem.core.storage


interface TokenStorage {
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clear()
    /** Performs a token refresh; return the new access token or null on failure. */
    suspend fun refreshToken(): String?
}
