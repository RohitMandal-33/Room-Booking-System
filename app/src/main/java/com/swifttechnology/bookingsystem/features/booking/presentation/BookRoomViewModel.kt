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
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

data class InternalMember(
    val id: Long,
    val name: String,
    val email: String,
    val department: String
)

data class ExternalMember(
    val name: String,
    val email: String
)

data class RoomBookingFormState(
    val bookingId: Long? = null,
    val meetingTitle: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val meetingType: String = "",
    val meetingTypeId: Long? = null,
    val recurringType: String = "Does not repeat",
    val recurrenceEndDate: String = "",
    val selectedWeekDays: Set<String> = emptySet(),
    val description: String = "",
    val participants: List<InternalMember> = emptyList(),
    val selectedGroupIds: Set<Long> = emptySet(),
    val externalMembers: List<ExternalMember> = emptyList(),
    val selectedRoom: String = "",
    val selectedRoomId: Long? = null,
    val updateScope: String = "ALL"
)

data class BookRoomScreenUiState(
    val searchQuery: String = "",
    val participantSearchQuery: String = "",
    val formState: RoomBookingFormState = RoomBookingFormState(),
    val availableRooms: List<Room> = emptyList(),
    val availableParticipants: List<InternalMember> = emptyList(),
    val availableMeetingTypes: List<MeetingTypeDTO> = emptyList(),
    val isLoadingRooms: Boolean = false,
    val isSearchingParticipants: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class BookRoomEvent {
    object NavigateBack : BookRoomEvent()
}

@HiltViewModel
class BookRoomViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val roomRepository: RoomRepository,
    private val participantRepository: ParticipantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookRoomScreenUiState>(BookRoomScreenUiState())
    val uiState: StateFlow<BookRoomScreenUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BookRoomEvent>()
    val events: SharedFlow<BookRoomEvent> = _events.asSharedFlow()

    init {
        loadActiveRooms()
        loadParticipants()
        loadMeetingTypes()
    }

    private fun loadParticipants() {
        searchParticipants("")
    }

    private var searchJob: Job? = null
    fun onParticipantSearchQueryChanged(query: String) {
        val c = _uiState.value
        _uiState.value = BookRoomScreenUiState(
            searchQuery = c.searchQuery,
            participantSearchQuery = query,
            formState = c.formState,
            availableRooms = c.availableRooms,
            availableParticipants = c.availableParticipants,
            availableMeetingTypes = c.availableMeetingTypes,
            isLoadingRooms = c.isLoadingRooms,
            isSearchingParticipants = c.isSearchingParticipants,
            isSubmitting = c.isSubmitting,
            submitSuccess = c.submitSuccess,
            errorMessage = c.errorMessage
        )
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

    private fun loadMeetingTypes() {
        viewModelScope.launch {
            bookingRepository.getMeetingTypes()
                .onSuccess { types ->
                    _uiState.update { it.copy(availableMeetingTypes = types) }
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
                            amenities = dto.resourceNames.mapNotNull { res ->
                                RoomAmenity.fromResourceString(res)
                            },
                            resources = dto.resourceNames,
                            resourceIds = dto.resourceIds
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

    fun prefillBookingDetails(
        bookingId: Long? = null,
        roomName: String,
        date: String,
        startTime: String,
        endTime: String,
        meetingTitle: String = "",
        meetingType: String = "",
        description: String = "",
        internalParticipantIds: List<Long> = emptyList(),
        externalMembers: List<ExternalMember> = emptyList()
    ) {
        _uiState.update { current ->
            val room = current.availableRooms.find { it.name == roomName }

            // Find matching meeting type from available types
            val matchedType = current.availableMeetingTypes.find { 
                it.name.equals(meetingType, ignoreCase = true) 
            }

            // Map API meeting type (INTERNAL/CLIENT/EXECUTIVE) → display form (Internal/Client/Executive)
            val displayType = matchedType?.name ?: when (meetingType.uppercase()) {
                "INTERNAL"  -> "Internal"
                "CLIENT"    -> "Client"
                "EXECUTIVE" -> "Executive"
                else        -> meetingType
            }

            // Match internal participants by id from current available participants
            val prefillInternal = current.availableParticipants.filter { it.id in internalParticipantIds }

            current.copy(
                formState = current.formState.copy(
                    meetingTitle    = meetingTitle,
                    selectedRoom    = roomName,
                    selectedRoomId  = room?.id,
                    date            = date,
                    startTime       = startTime,
                    endTime         = endTime,
                    meetingType     = displayType,
                    meetingTypeId   = matchedType?.id,
                    description     = description,
                    participants    = prefillInternal,
                    externalMembers = externalMembers,
                    bookingId       = bookingId
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
                meetingTypeId = state.meetingTypeId ?: mapMeetingType(state.meetingType),
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

            val result = if (state.bookingId != null) {
                bookingRepository.updateBooking(state.bookingId, request)
            } else {
                bookingRepository.bookRoom(request)
            }

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitSuccess = true,
                        formState = RoomBookingFormState() // Reset form
                    )
                }
                _events.emit(BookRoomEvent.NavigateBack)
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "Action failed. Please try again."
                    )
                }
            }
        }
    }

    fun clearSubmitSuccess() {
        _uiState.update { it.copy(submitSuccess = false) }
    }

    fun clearError() {
        _uiState.update { state -> state.copy(errorMessage = null) }
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
     * Parses time string to API format "HH:mm:ss".
     * Supports formats like "HH:mm" (24h) and "hh:mm a" (12h with AM/PM).
     */
    private fun parseTimeString(time: String): String? {
        if (time.isBlank()) return null
        return try {
            // Check if format is "hh:mm a" (e.g. "10:30 AM")
            if (time.contains("AM", ignoreCase = true) || time.contains("PM", ignoreCase = true)) {
                val inputFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                val outputFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                val date = inputFormat.parse(time) ?: return null
                outputFormat.format(date)
            } else {
                // Assume format is "HH:mm"
                val parts = time.split(":")
                val h = parts[0].padStart(2, '0')
                val m = parts[1].padStart(2, '0')
                "$h:$m:00"
            }
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
     * Maps display meeting type to API id.
     */
    private fun mapMeetingType(displayType: String): Long? {
        // Fallback for hardcoded values
        return when (displayType) {
            "Internal" -> 1L
            "Client" -> 2L
            "Executive" -> 3L
            else -> if (displayType.isBlank()) null else 1L
        }
    }

    /**
     * Maps display recurring type to API enum value.
     */
    private fun mapRecurringType(displayType: String): String = when (displayType) {
        "Every weekday (Sun-Fri)" -> "WEEKDAY"
        "Daily" -> "DAILY"
        "Weekly" -> "WEEKLY"
        "Monthly" -> "MONTHLY"
        "Yearly" -> "YEARLY"
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

