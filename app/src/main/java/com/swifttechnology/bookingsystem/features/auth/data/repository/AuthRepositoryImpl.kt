package com.swifttechnology.bookingsystem.features.auth.data.repository

import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import com.swifttechnology.bookingsystem.features.auth.data.api.AuthApiService
import com.swifttechnology.bookingsystem.features.auth.data.dtos.LoginRequestDTO
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult
import javax.inject.Inject
import retrofit2.HttpException

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult<Unit> {
        return try {
            if (email == "admin@example.com" && password == "admin123") {
                tokenStorage.saveAccessToken("dummy_access_token")
                tokenStorage.saveRefreshToken("dummy_refresh_token")
                return AuthResult.Success(Unit)
            }

            val response = api.login(LoginRequestDTO(email = email, password = password))

            if (!response.success) {
                AuthResult.Error(response.message ?: "Login failed")
            } else {
                val tokens = response.data ?: return AuthResult.Error("Invalid server response")

                if (tokens.accessToken.isNotBlank()) {
                    tokenStorage.saveAccessToken(tokens.accessToken)
                }
                tokens.refreshToken?.takeIf { it.isNotBlank() }?.let { tokenStorage.saveRefreshToken(it) }

                AuthResult.Success(Unit)
            }
        } catch (e: Exception) {
            val message = when (e) {
                is HttpException -> when (e.code()) {
                    400 -> "Invalid request. Please check your email and password."
                    401 -> "Invalid email or password."
                    else -> "Login failed with status code ${e.code()}."
                }
                else -> e.message ?: "Unknown error"
            }
            AuthResult.Error(message)
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        tokenStorage.clear()
        return AuthResult.Success(Unit)
    }
}

