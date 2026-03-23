package com.swifttechnology.bookingsystem.core.network

import retrofit2.http.Body
import retrofit2.http.POST

// Minimal Retrofit interface used only by [com.swifttechnology.bookingsystem.core.storage.TokenDataStore] to refresh access tokens. Kept in core/network to avoid circular dependency with feature modules.

interface RefreshTokenApi {

    @POST(APIEndpoint.REFRESH_TOKEN)
    suspend fun refreshAccessToken(
        @Body request: AccessTokenRequest
    ): GlobalResponse<AccessTokenResponse>
}

// Request body for the token refresh endpoint.
data class AccessTokenRequest(val refreshToken: String)

// Response payload from the token refresh endpoint.
data class AccessTokenResponse(val accessToken: String)
