package com.swifttechnology.bookingsystem.features.auth.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.auth.data.dtos.*
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {

    @POST(APIEndpoint.LOGIN)
    suspend fun login(@Body request: LoginRequestDTO): GlobalResponse<AuthResponseDTO>

    @POST(APIEndpoint.LOGOUT)
    suspend fun logout(@Body request: AccessTokenRequestDTO): GlobalResponse<Unit>

    @POST(APIEndpoint.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body request: ForgotPasswordOtpRequestDTO): GlobalResponse<Unit>

    @POST(APIEndpoint.VERIFY_OTP)
    suspend fun verifyOtp(@Body request: OtpVerifyRequestDTO): GlobalResponse<OtpVerifyResponseDTO>

    @PUT(APIEndpoint.RESET_PASSWORD)
    suspend fun resetPassword(@Body request: PasswordForgotRequestDTO): GlobalResponse<Unit>

    @PUT(APIEndpoint.CHANGE_PASSWORD)
    suspend fun changePassword(@Body request: UpdatePasswordRequestDTO): GlobalResponse<Unit>

    @POST(APIEndpoint.RESEND_OTP)
    suspend fun resendOtp(@Body request: ResendOtpRequestDTO): GlobalResponse<Unit>
}
