package com.swifttechnology.bookingsystem.core.auth

import com.swifttechnology.bookingsystem.features.auth.domain.models.User

sealed class AuthState {
    // Token exists and user is authenticated.
    data class Authenticated(val user: User) : AuthState()

    // No token found — show login/register screen.
    object Unauthenticated : AuthState()

    // App is determining the initial auth state on startup.
    object Loading : AuthState()
}
