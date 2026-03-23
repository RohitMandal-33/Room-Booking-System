package com.swifttechnology.bookingsystem.features.auth.domain.usecases

import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult
import javax.inject.Inject

class ResendOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): AuthResult<Unit> =
        repository.resendOtp(email)
}
