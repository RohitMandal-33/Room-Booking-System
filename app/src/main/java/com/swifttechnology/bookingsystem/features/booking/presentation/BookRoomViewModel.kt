package com.swifttechnology.bookingsystem.features.booking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomAmenity
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.booking.data.dtos.ExternalParticipantDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.RoomBookingRequestDTO
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository.RoomRepository
import com.swifttechnology.bookingsystem.features.participants.domain.repository.ParticipantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@HiltViewModel
class BookRoomViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val roomRepository: RoomRepository,
    private val participantRepository: ParticipantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookRoomUiState())
    val uiState: StateFlow<BookRoomUiState> = _uiState.asStateFlow()

    init {
        loadActiveRooms()
        loadParticipants()
    }

    private fun loadParticipants() {
        searchParticipants("")
    }

    private var searchJob: Job? = null
    fun onParticipantSearchQueryChanged(query: String) {
        _uiState.update { it.copy(participantSearchQuery = query) }
        searchParticipants(query)
    }

    private fun searchParticipants(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) {
                delay(300) // Debounce search
            }
            _uiState.update { it.copy(isSearchingParticipants = true) }
            participantRepository.searchParticipants(query).collect { participants ->
                val internalMembers = participants.map { 
                    InternalMember(
                        id = it.id,
                        name = it.name,
                        email = it.email,
                        department = it.department
                    )
                }
                _uiState.update { 
                    it.copy(
                        availableParticipants = internalMembers,
                        isSearchingParticipants = false
                    ) 
                }
            }
        }
    }

    private fun loadActiveRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRooms = true) }
            roomRepository.listActiveRooms()
                .onSuccess { page ->
                    val rooms = page.content?.map { dto ->
                        Room(
                            id = dto.id,
                            name = dto.roomName,
                            status = RoomStatus.fromApiString(dto.status),
                            capacity = dto.capacity,
                            amenities = (dto.resources ?: emptyList()).mapNotNull { res ->
                                RoomAmenity.fromResourceString(res)
                            },
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


    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onFormStateChanged(formState: RoomBookingFormState) {
        _uiState.update { it.copy(formState = formState) }
    }

    fun preSelectRoom(roomName: String) {
        val currentDate = java.text.SimpleDateFormat(
            "dd MMM yyyy", java.util.Locale.getDefault()
        ).format(java.util.Date())

        _uiState.update { current ->
            val room = current.availableRooms.find { it.name == roomName }
            current.copy(
                formState = current.formState.copy(
                    selectedRoom = roomName,
                    selectedRoomId = room?.id,
                    date = currentDate
                )
            )
        }
    }

    fun prefillBookingDetails(roomName: String, date: String, startTime: String, endTime: String) {
        _uiState.update { current ->
            val room = current.availableRooms.find { it.name == roomName }
            current.copy(
                formState = current.formState.copy(
                    selectedRoom = roomName,
                    selectedRoomId = room?.id,
                    date = date,
                    startTime = startTime,
                    endTime = endTime
                )
            )
        }
    }

    fun submitBooking() {
        val state = _uiState.value.formState

        // Validate required fields before submitting
        val validationError = validateForm(state)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val request = RoomBookingRequestDTO(
                meetingTitle = state.meetingTitle.ifBlank { null },
                date = convertDisplayDateToApiDate(state.date),
                startTime = parseTimeString(state.startTime),
                endTime = parseTimeString(state.endTime),
                meetingType = mapMeetingType(state.meetingType),
                description = state.description.ifBlank { null },
                roomId = state.selectedRoomId,
                internalParticipantIds = state.participants
                    .takeIf { it.isNotEmpty() }
                    ?.map { it.id }
                    ?.takeIf { it.isNotEmpty() },
                externalParticipants = state.externalMembers
                    .takeIf { it.isNotEmpty() }
                    ?.map { ExternalParticipantDTO(name = it.name, email = it.email) },
                recurrenceType = mapRecurringType(state.recurringType),
                recurrenceEndDate = if (state.recurringType != "Does not repeat") convertDisplayDateToApiDate(state.recurrenceEndDate) else null,
                weekDays = if (state.recurringType == "Custom") {
                    state.selectedWeekDays.map { mapWeekDay(it) }.toList()
                } else null
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
                            errorMessage = error.message ?: "Booking failed. Please try again."
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
     * Parses "HH:mm" time string to API format "HH:mm:ss".
     */
    private fun parseTimeString(time: String): String? {
        if (time.isBlank()) return null
        return try {
            val parts = time.split(":")
            String.format(java.util.Locale.US, "%02d:%02d:00", parts[0].toInt(), parts[1].toInt())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Validates that all required booking fields are filled.
     */
    private fun validateForm(state: RoomBookingFormState): String? {
        return when {
            state.meetingTitle.isBlank() -> "Meeting title is required"
            state.selectedRoomId == null -> "Please select a room"
            state.date.isBlank() -> "Please select a date"
            state.startTime.isBlank() -> "Please select a start time"
            state.endTime.isBlank() -> "Please select an end time"
            state.meetingType.isBlank() -> "Please select a meeting type"
            state.recurringType != "Does not repeat" && state.recurrenceEndDate.isBlank() -> "Please select an end date for recurrence"
            state.recurringType == "Custom" && state.selectedWeekDays.isEmpty() -> "Please select at least one day for custom recurrence"
            else -> null
        }
    }

    /**
     * Maps display meeting type to API enum value.
     */
    private fun mapMeetingType(displayType: String): String? = when (displayType) {
        "Internal" -> "INTERNAL"
        "Client" -> "CLIENT"
        "Executive" -> "EXECUTIVE"
        else -> if (displayType.isBlank()) null else "INTERNAL"
    }

    /**
     * Maps display recurring type to API enum value.
     */
    private fun mapRecurringType(displayType: String): String = when (displayType) {
        "Every weekday (Sun-Fri)" -> "WEEKDAY"
        "Daily" -> "DAILY"
        "Weekly" -> "WEEKLY"
        "Monthly" -> "MONTHLY"
        "Custom" -> "CUSTOM"
        else -> "NONE"
    }

    /**
     * Maps day abbreviation to API enum value.
     */
    private fun mapWeekDay(day: String): String = when (day) {
        "Sun" -> "SUNDAY"
        "Mon" -> "MONDAY"
        "Tues" -> "TUESDAY"
        "Wed" -> "WEDNESDAY"
        "Thu" -> "THURSDAY"
        "Fri" -> "FRIDAY"
        "Sat" -> "SATURDAY"
        else -> "MONDAY" // Fallback
    }
}
