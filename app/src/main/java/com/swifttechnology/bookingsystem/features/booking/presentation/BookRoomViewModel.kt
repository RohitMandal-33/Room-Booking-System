package com.swifttechnology.bookingsystem.features.booking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.booking.data.dtos.ExternalParticipantDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.LocalTimeDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.RoomBookingRequestDTO
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository.RoomRepository
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BookRoomViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookRoomUiState())
    val uiState: StateFlow<BookRoomUiState> = _uiState.asStateFlow()

    init {
        loadActiveRooms()
    }

    private fun loadActiveRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRooms = true) }
            roomRepository.listActiveRooms()
                .onSuccess { page ->
                    val rooms = page.data?.map { dto ->
                        Room(
                            id = dto.id,
                            name = dto.roomName,
                            status = RoomStatus.fromApiString(dto.status),
                            capacity = dto.capacity,
                            resources = dto.resources ?: emptyList()
                        )
                    } ?: emptyList()
                    _uiState.update { it.copy(availableRooms = rooms, isLoadingRooms = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingRooms = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

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

    fun onFormStateChanged(formState: RoomBookingFormState) {
        _uiState.update { it.copy(formState = formState) }
    }

    fun submitBooking() {
        val state = _uiState.value.formState
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val request = RoomBookingRequestDTO(
                meetingTitle = state.meetingTitle.ifBlank { null },
                date = convertDisplayDateToApiDate(state.date),
                startTime = parseTimeString(state.startTime),
                endTime = parseTimeString(state.endTime),
                meetingType = mapMeetingType(state.meetingType),
                roomId = state.selectedRoomId,
                externalParticipants = state.externalMembers
                    .takeIf { it.isNotEmpty() }
                    ?.map { ExternalParticipantDTO(name = it.name, email = it.email) }
            )

            bookingRepository.bookRoom(request)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitSuccess = true,
                            formState = RoomBookingFormState() // Reset form
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun clearSubmitSuccess() {
        _uiState.update { it.copy(submitSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    suspend fun logout() {
        authRepository.logout()
    }

    /**
     * Converts display date format "dd MMM yyyy" to API format "yyyy-MM-dd"
     */
    private fun convertDisplayDateToApiDate(displayDate: String): String? {
        if (displayDate.isBlank()) return null
        return try {
            val inputFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val date = inputFormat.parse(displayDate) ?: return null
            outputFormat.format(date)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parses "HH:mm" time string to LocalTimeDTO.
     */
    private fun parseTimeString(time: String): LocalTimeDTO? {
        if (time.isBlank()) return null
        return try {
            val parts = time.split(":")
            LocalTimeDTO(hour = parts[0].toInt(), minute = parts[1].toInt())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Maps display meeting type to API enum value.
     */
    private fun mapMeetingType(displayType: String): String? = when (displayType) {
        "Team Meeting" -> "INTERNAL"
        "Client Call" -> "CLIENT"
        "Workshop", "Training" -> "INTERNAL"
        "Interview" -> "EXECUTIVE"
        else -> if (displayType.isBlank()) null else "INTERNAL"
    }
}
