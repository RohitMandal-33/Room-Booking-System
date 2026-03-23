package com.swifttechnology.bookingsystem.features.auth.domain.repository

import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult

/**
 * Auth domain repository contract — what the use cases need, nothing more.
 * AuthRepositoryImpl provides the actual implementation.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<Unit>
    suspend fun logout(): AuthResult<Unit>
    suspend fun forgotPassword(email: String): AuthResult<Unit>
    suspend fun verifyOtp(email: String, otp: String): AuthResult<String>  // returns referenceId
    suspend fun resetPassword(referenceId: String, newPassword: String, confirmNewPassword: String): AuthResult<Unit>
    suspend fun changePassword(oldPassword: String, newPassword: String, confirmNewPassword: String): AuthResult<Unit>
    suspend fun resendOtp(email: String): AuthResult<Unit>
}
