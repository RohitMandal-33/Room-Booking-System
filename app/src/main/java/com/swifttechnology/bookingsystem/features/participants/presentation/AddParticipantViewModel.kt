package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddParticipantUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddParticipantViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddParticipantUiState())
    val uiState: StateFlow<AddParticipantUiState> = _uiState.asStateFlow()

    fun createParticipant(
        firstname: String,
        lastname: String,
        email: String,
        position: String,
        phoneNo: String,
        password: String,
        role: String,
        department: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            
            // Map the dropdown strings to mock or fallback dummy IDs since we don't have dynamic IDs fetched
            val roleId = when (role.lowercase()) {
                "admin" -> 1L
                "manager" -> 2L
                else -> 3L // user/staff
            }

            val departmentId = when (department.lowercase()) {
                "mobile app" -> 1L
                "web development" -> 2L
                "hr" -> 3L
                "design" -> 4L
                else -> 1L
            }

            val result = userRepository.createUser(
                firstname = firstname,
                lastname = lastname,
                email = email,
                position = position,
                phoneNo = phoneNo,
                password = password,
                roleId = roleId,
                departmentId = departmentId
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Failed to add member") }
            }
        }
    }

    fun resetState() {
        _uiState.update { AddParticipantUiState() }
    }
}
