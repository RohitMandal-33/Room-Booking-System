package com.swifttechnology.bookingsystem.features.auth.domain.repository

import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult

/**
 * Auth domain repository contract — what the use cases need, nothing more.
 * AuthRepositoryImpl provides the actual implementation.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<Unit>
    suspend fun logout(): AuthResult<Unit>
}
