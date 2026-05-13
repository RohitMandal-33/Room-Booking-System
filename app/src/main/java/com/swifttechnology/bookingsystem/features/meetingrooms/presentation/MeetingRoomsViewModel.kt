package com.swifttechnology.bookingsystem.features.meetingrooms.presentation

import androidx.annotation.RequiresApi
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.core.utils.ErrorMapper
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.core.model.RoomAmenity
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository.RoomRepository
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import com.swifttechnology.bookingsystem.core.utils.DateTimeUtils

sealed class MeetingRoomsEvent {
    data class ShowSnackbar(val message: String) : MeetingRoomsEvent()
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MeetingRoomsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val roomRepository: RoomRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeetingRoomsUiState())
    val uiState: StateFlow<MeetingRoomsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MeetingRoomsEvent>()
    val uiEvent: SharedFlow<MeetingRoomsEvent> = _uiEvent.asSharedFlow()

    private val _navigationEvents = MutableSharedFlow<MeetingRoomsNavigationEvent>()
    val navigationEvents: SharedFlow<MeetingRoomsNavigationEvent> = _navigationEvents.asSharedFlow()

    init {
        loadRooms()
        loadResources()
    }

    fun loadResources() {
        viewModelScope.launch {
            roomRepository.getAllResources()
                .onSuccess { resources ->
                    _uiState.update { it.copy(allResources = resources) }
                }
        }
    }

    fun loadRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // 1. Fetch Rooms
            val roomsResult = roomRepository.listAllRooms()
            
            // 2. Fetch Today's Meetings to check for current overlaps
            val todayStr = LocalDate.now().toString() // yyyy-MM-dd
            val meetingsResult = bookingRepository.getCalendarDay(todayStr)
            
            val todayMeetings = meetingsResult.getOrDefault(emptyList())
            val now = LocalTime.now()

            roomsResult.onSuccess { page ->
                val rooms = page.content?.map { dto ->
                    val mappedAmenities = dto.resourceNames.mapNotNull { res ->
                        RoomAmenity.fromResourceString(res)
                    }
                    
                    // Check if this room has a meeting happening right now
                    val isCurrentlyBooked = todayMeetings.any { meeting ->
                        val meetingRoomId = meeting.roomId ?: meeting.room?.id
                        if (meetingRoomId == dto.id) {
                            val start = DateTimeUtils.parseLocalTime(meeting.startTime)
                            val end = DateTimeUtils.parseLocalTime(meeting.endTime)
                            if (start != null && end != null) {
                                !now.isBefore(start) && now.isBefore(end)
                            } else false
                        } else false
                    }

                    val effectiveStatus = if (isCurrentlyBooked) {
                        RoomStatus.BOOKED
                    } else {
                        RoomStatus.fromApiString(dto.status)
                    }

                    Room(
                        id = dto.id,
                        name = dto.roomName,
                        status = effectiveStatus,
                        capacity = dto.capacity,
                        amenities = mappedAmenities,
                        resources = dto.resourceNames,
                        resourceIds = dto.resourceIds
                    )
                } ?: emptyList()
                
                _uiState.update { current -> 
                    val fetchedNames = rooms.map { it.name }.toSet()
                    val missingTempRooms = current.rooms.filter { it.id == 0L && it.name !in fetchedNames }
                    
                    current.copy(
                        rooms = missingTempRooms + rooms,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = ErrorMapper.map(error)) }
            }
        }
    }

    fun addRoom(name: String, capacity: Int, resourceIds: List<Long>?) {
        viewModelScope.launch {
            val tempRoom = Room(
                id = 0L,
                name = name,
                status = RoomStatus.ACTIVE,
                capacity = capacity,
                timeLabel = "New",
                resourceIds = resourceIds ?: emptyList()
            )
            _uiState.update { current ->
                current.copy(
                    isLoading = true, 
                    errorMessage = null,
                    rooms = current.rooms + tempRoom
                )
            }
            roomRepository.addRoom(name, capacity, resourceIds)
                .onSuccess {
                    loadRooms()
                    _uiEvent.emit(MeetingRoomsEvent.ShowSnackbar("Room added successfully!"))
                    _navigationEvents.emit(MeetingRoomsNavigationEvent.NavigateBack)
                }
                .onFailure { error ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = ErrorMapper.map(error),
                            rooms = current.rooms - tempRoom
                        )
                    }
                    _uiEvent.emit(MeetingRoomsEvent.ShowSnackbar(ErrorMapper.map(error)))
                }
        }
    }

    fun updateRoom(original: Room, updated: Room) {
        viewModelScope.launch {
            // Optimistic UI update
            _uiState.update { current ->
                current.copy(
                    rooms = current.rooms.map { if (it == original) updated else it }
                )
            }
            // API call
            if (updated.id > 0) {
                roomRepository.updateRoom(
                    id = updated.id,
                    roomName = updated.name,
                    capacity = updated.capacity,
                    resourceIds = updated.resourceIds.ifEmpty { null }
                ).onSuccess {
                    _uiEvent.emit(MeetingRoomsEvent.ShowSnackbar("Room updated successfully!"))
                    _navigationEvents.emit(MeetingRoomsNavigationEvent.NavigateBack)
                }.onFailure {
                    // Revert on failure
                    _uiState.update { current ->
                        current.copy(
                            rooms = current.rooms.map { if (it == updated) original else it },
                            errorMessage = ErrorMapper.map(it)
                        )
                    }
                }
            }
        }
    }

    fun toggleRoomStatus(room: Room) {
        viewModelScope.launch {
            val newStatus = if (room.status == RoomStatus.INACTIVE || room.status == RoomStatus.DISABLED) {
                RoomStatus.ACTIVE
            } else {
                RoomStatus.INACTIVE
            }
            val newApiStatus = if (newStatus == RoomStatus.ACTIVE) "ACTIVE" else "INACTIVE"

            // Optimistic UI update
            _uiState.update { current ->
                current.copy(
                    rooms = current.rooms.map { if (it == room) it.copy(status = newStatus) else it }
                )
            }

            if (room.id > 0) {
                roomRepository.changeRoomStatus(room.id, newApiStatus)
                    .onSuccess {
                        val msg = if (newStatus == RoomStatus.ACTIVE) "Room enabled successfully!" else "Room disabled successfully!"
                        _uiEvent.emit(MeetingRoomsEvent.ShowSnackbar(msg))
                    }
                    .onFailure {
                        // Revert on failure
                        _uiState.update { current ->
                            current.copy(
                                rooms = current.rooms.map { if (it.status == newStatus && it.id == room.id) room else it }
                            )
                        }
                        _uiEvent.emit(MeetingRoomsEvent.ShowSnackbar(ErrorMapper.map(it)))
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

    suspend fun logout() {
        authRepository.logout()
    }

    fun addResource(name: String) {
        viewModelScope.launch {
            roomRepository.addResource(name)
                .onSuccess {
                    loadResources()
                }
                .onFailure { error ->
                    _uiEvent.emit(MeetingRoomsEvent.ShowSnackbar(ErrorMapper.map(error)))
                }
        }
    }
}

sealed class MeetingRoomsNavigationEvent {
    object NavigateBack : MeetingRoomsNavigationEvent()
}
