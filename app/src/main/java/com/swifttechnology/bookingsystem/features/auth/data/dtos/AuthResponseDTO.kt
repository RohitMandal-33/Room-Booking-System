package com.swifttechnology.bookingsystem.features.auth.data.dtos

import com.google.gson.annotations.SerializedName

data class AuthResponseDTO(
    @SerializedName("accessToken", alternate = ["access_token"])
    val accessToken: String,
    @SerializedName("refreshToken", alternate = ["refresh_token"])
    val refreshToken: String?
)

