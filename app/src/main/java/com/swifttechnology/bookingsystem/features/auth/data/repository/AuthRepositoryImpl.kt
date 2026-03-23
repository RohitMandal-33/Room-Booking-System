package com.swifttechnology.bookingsystem.features.auth.data.repository

import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import com.swifttechnology.bookingsystem.features.auth.data.api.AuthApiService
import com.swifttechnology.bookingsystem.features.auth.data.dtos.*
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
            // Dev bypass — kept intentionally for local testing
            if (email == "admin@example.com" && password == "admin123") {
                tokenStorage.saveAccessToken("dummy_access_token")
                tokenStorage.saveRefreshToken("dummy_refresh_token")
                return AuthResult.Success(Unit)
            }

            val response = api.login(LoginRequestDTO(email = email, password = password))

            if (!response.success) {
                AuthResult.Error(response.message)
            } else {
                val tokens = response.data ?: return AuthResult.Error("Invalid server response")

                if (tokens.accessToken.isNotBlank()) {
                    tokenStorage.saveAccessToken(tokens.accessToken)
                }
                tokens.refreshToken?.takeIf { it.isNotBlank() }?.let { tokenStorage.saveRefreshToken(it) }

                AuthResult.Success(Unit)
            }
        } catch (e: Exception) {
            AuthResult.Error(mapException(e))
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            val refreshToken = tokenStorage.getRefreshToken()
            if (!refreshToken.isNullOrBlank()) {
                api.logout(AccessTokenRequestDTO(refreshToken = refreshToken))
            }
            tokenStorage.clear()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            // Still clear local tokens even if server call fails
            tokenStorage.clear()
            AuthResult.Success(Unit)
        }
    }

    override suspend fun forgotPassword(email: String): AuthResult<Unit> {
        return try {
            val response = api.forgotPassword(ForgotPasswordOtpRequestDTO(email = email))
            if (response.success) AuthResult.Success(Unit)
            else AuthResult.Error(response.message)
        } catch (e: Exception) {
            AuthResult.Error(mapException(e))
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): AuthResult<String> {
        return try {
            val response = api.verifyOtp(OtpVerifyRequestDTO(email = email, otp = otp))
            if (response.success && response.data != null) {
                AuthResult.Success(response.data.referenceId)
            } else {
                AuthResult.Error(response.message)
            }
        } catch (e: Exception) {
            AuthResult.Error(mapException(e))
        }
    }

    override suspend fun resetPassword(
        referenceId: String,
        newPassword: String,
        confirmNewPassword: String
    ): AuthResult<Unit> {
        return try {
            val response = api.resetPassword(
                PasswordForgotRequestDTO(
                    referenceId = referenceId,
                    newPassword = newPassword,
                    confirmNewPassword = confirmNewPassword
                )
            )
            if (response.success) AuthResult.Success(Unit)
            else AuthResult.Error(response.message)
        } catch (e: Exception) {
            AuthResult.Error(mapException(e))
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): AuthResult<Unit> {
        return try {
            val response = api.changePassword(
                UpdatePasswordRequestDTO(
                    oldPassword = oldPassword,
                    newPassword = newPassword,
                    confirmNewPassword = confirmNewPassword
                )
            )
            if (response.success) AuthResult.Success(Unit)
            else AuthResult.Error(response.message)
        } catch (e: Exception) {
            AuthResult.Error(mapException(e))
        }
    }

    override suspend fun resendOtp(email: String): AuthResult<Unit> {
        return try {
            val response = api.resendOtp(
                ResendOtpRequestDTO(email = email, otpPurpose = "FORGOT_PASSWORD")
            )
            if (response.success) AuthResult.Success(Unit)
            else AuthResult.Error(response.message)
        } catch (e: Exception) {
            AuthResult.Error(mapException(e))
        }
    }

    /** Maps common exceptions to user-friendly error messages. */
    private fun mapException(e: Exception): String = when (e) {
        is HttpException -> when (e.code()) {
            400 -> "Invalid request. Please check your input."
            401 -> "Invalid credentials."
            403 -> "Access denied."
            else -> "Request failed with status code ${e.code()}."
        }
        else -> e.message ?: "An unexpected error occurred."
    }
}
