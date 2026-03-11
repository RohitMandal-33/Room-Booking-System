package com.swifttechnology.bookingsystem.features.auth.domain.util

sealed class AuthResult<out T> {
    data object Loading : AuthResult<Nothing>()
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

