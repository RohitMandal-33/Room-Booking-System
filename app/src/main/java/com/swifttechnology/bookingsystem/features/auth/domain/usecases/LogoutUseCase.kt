package com.swifttechnology.bookingsystem.features.auth.domain.usecases

import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult
import javax.inject.Inject

/** Use case: logout and clear all stored tokens. */
class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val tokenStorage: TokenStorage
) {
    suspend operator fun invoke(): AuthResult<Unit> {
        tokenStorage.clear()
        return repository.logout()
    }
}
