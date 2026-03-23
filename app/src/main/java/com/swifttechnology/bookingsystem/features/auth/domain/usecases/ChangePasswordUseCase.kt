package com.swifttechnology.bookingsystem.features.auth.domain.usecases

import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        oldPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): AuthResult<Unit> =
        repository.changePassword(oldPassword, newPassword, confirmNewPassword)
}
