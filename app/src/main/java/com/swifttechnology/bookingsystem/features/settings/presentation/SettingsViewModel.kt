package com.swifttechnology.bookingsystem.features.settings.presentation

import androidx.lifecycle.ViewModel
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.swifttechnology.bookingsystem.features.auth.domain.util.AuthResult
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.department.domain.repository.DepartmentRepository
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onSidebarItemSelected(item: SidebarItem) {
        _uiState.update { current ->
            current.copy(
                sidebarItems = current.sidebarItems.map { it.copy(isActive = it.route == item.route) },
                selectedItem = item.copy(isActive = true)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    init {
        fetchMeetingTypes()
    }

    private fun fetchMeetingTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            bookingRepository.getMeetingTypes()
                .onSuccess { types ->
                    _uiState.update { it.copy(meetingTypes = types, isLoading = false) }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(error = err.message, isLoading = false) }
                }
        }
    }

    fun addMeetingType(name: String, colorCode: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            bookingRepository.createMeetingType(MeetingTypeRequestDTO(name, colorCode, "ACTIVE"))
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Meeting type added successfully", isLoading = false) }
                    fetchMeetingTypes()
                }
                .onFailure { err ->
                    _uiState.update { it.copy(error = err.message, isLoading = false) }
                }
        }
    }

    fun updateMeetingType(id: Long, name: String, colorCode: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            bookingRepository.updateMeetingType(id, MeetingTypeRequestDTO(name, colorCode, "ACTIVE"))
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Meeting type updated successfully", isLoading = false) }
                    fetchMeetingTypes()
                }
                .onFailure { err ->
                    _uiState.update { it.copy(error = err.message, isLoading = false) }
                }
        }
    }

    fun changeMeetingTypeStatus(id: Long, currentStatus: String) {
        val newStatus = if (currentStatus == "ACTIVE") "INACTIVE" else "ACTIVE"
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            bookingRepository.changeMeetingTypeStatus(id, newStatus)
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Status updated to $newStatus", isLoading = false) }
                    fetchMeetingTypes()
                }
                .onFailure { err ->
                    _uiState.update { it.copy(error = err.message, isLoading = false) }
                }
        }
    }

    fun updateGlobalRules(duration: String, buffer: String, smartConflict: Boolean) {
        _uiState.update {
            it.copy(
                durationMin = duration,
                bufferMin = buffer,
                smartConflict = smartConflict
            )
        }
    }

    fun changePassword(old: String, new: String, confirm: String) {
        if (old.isBlank() || new.isBlank() || confirm.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.changePassword(old, new, confirm)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(successMessage = "Password changed successfully", isLoading = false) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(error = result.message, isLoading = false) }
                }
                is AuthResult.Loading -> {
                    // Do nothing
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    suspend fun logout() {
        authRepository.logout()
    }
}

