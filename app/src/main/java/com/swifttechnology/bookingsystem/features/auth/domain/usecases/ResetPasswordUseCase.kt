package com.swifttechnology.bookingsystem.features.auth.domain.usecases

import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        referenceId: String,
        newPassword: String,
        confirmNewPassword: String
    ): AuthResult<Unit> =
        repository.resetPassword(referenceId, newPassword, confirmNewPassword)
}
