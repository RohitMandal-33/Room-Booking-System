package com.swifttechnology.bookingsystem.features.auth.domain.usecases

import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    /** Returns the referenceId on success. */
    suspend operator fun invoke(email: String, otp: String): AuthResult<String> =
        repository.verifyOtp(email, otp)
}
