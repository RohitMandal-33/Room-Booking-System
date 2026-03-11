package com.swifttechnology.bookingsystem.features.auth.data.api

import com.swifttechnology.bookingsystem.features.auth.data.dtos.ApiResponse
import com.swifttechnology.bookingsystem.features.auth.data.dtos.AuthResponseDTO
import com.swifttechnology.bookingsystem.features.auth.data.dtos.LoginRequestDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/login")
    suspend fun login(@Body request: LoginRequestDTO): ApiResponse<AuthResponseDTO>
}

