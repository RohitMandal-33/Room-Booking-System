package com.swifttechnology.bookingsystem.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.auth.domain.usecases.ForgotPasswordUseCase
import com.swifttechnology.bookingsystem.features.auth.domain.usecases.ResendOtpUseCase
import com.swifttechnology.bookingsystem.features.auth.domain.usecases.ResetPasswordUseCase
import com.swifttechnology.bookingsystem.features.auth.domain.usecases.VerifyOtpUseCase
import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val email: String = "",
    val otp: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val referenceId: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val otpError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false
)

sealed class ForgotPasswordEvent {
    data object NavigateToOtp : ForgotPasswordEvent()
    data object NavigateToResetPassword : ForgotPasswordEvent()
    data object NavigateToLogin : ForgotPasswordEvent()
    data class ShowSnackbar(val message: String) : ForgotPasswordEvent()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val resendOtpUseCase: ResendOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _events = Channel<ForgotPasswordEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onOtpChanged(value: String) {
        _uiState.update { it.copy(otp = value, otpError = null) }
    }

    fun onNewPasswordChanged(value: String) {
        _uiState.update { it.copy(newPassword = value, passwordError = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun sendOtp() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = forgotPasswordUseCase(email)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(ForgotPasswordEvent.NavigateToOtp)
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(ForgotPasswordEvent.ShowSnackbar(result.message))
                }
                AuthResult.Loading -> {}
            }
        }
    }

    fun verifyOtp() {
        val state = _uiState.value
        if (state.otp.length < 6) {
            _uiState.update { it.copy(otpError = "Enter valid 6-digit OTP") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = verifyOtpUseCase(state.email, state.otp)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, referenceId = result.data) }
                    _events.send(ForgotPasswordEvent.NavigateToResetPassword)
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(ForgotPasswordEvent.ShowSnackbar(result.message))
                }
                AuthResult.Loading -> {}
            }
        }
    }

    fun resetPassword() {
        val state = _uiState.value
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = resetPasswordUseCase(state.referenceId, state.newPassword, state.confirmPassword)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(ForgotPasswordEvent.ShowSnackbar("Password reset successful"))
                    _events.send(ForgotPasswordEvent.NavigateToLogin)
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(ForgotPasswordEvent.ShowSnackbar(result.message))
                }
                AuthResult.Loading -> {}
            }
        }
    }

    fun resendOtp() {
        viewModelScope.launch {
            when (val result = resendOtpUseCase(_uiState.value.email)) {
                is AuthResult.Success -> {
                    _events.send(ForgotPasswordEvent.ShowSnackbar("OTP resent successfully"))
                }
                is AuthResult.Error -> {
                    _events.send(ForgotPasswordEvent.ShowSnackbar(result.message))
                }
                AuthResult.Loading -> {}
            }
        }
    }
}
