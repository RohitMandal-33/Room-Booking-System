package com.swifttechnology.bookingsystem.features.meetingrooms.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.core.model.RoomAmenity
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository.RoomRepository
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

sealed class MeetingRoomsEvent {
    data class ShowSnackbar(val message: String) : MeetingRoomsEvent()
}

@HiltViewModel
class MeetingRoomsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val roomRepository: RoomRepository
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
            roomRepository.listAllRooms()
                .onSuccess { page ->
                    val rooms = page.content?.map { dto ->
                        val mappedAmenities = dto.resourceNames.mapNotNull { res ->
                            RoomAmenity.fromResourceString(res)
                        }
                        Room(
                            id = dto.id,
                            name = dto.roomName,
                            status = RoomStatus.fromApiString(dto.status),
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
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
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
                            errorMessage = error.message,
                            rooms = current.rooms - tempRoom
                        )
                    }
                    _uiEvent.emit(MeetingRoomsEvent.ShowSnackbar(error.message ?: "Failed to add room"))
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
                            errorMessage = it.message
                        )
                    }
                }
            }
        }
    }

    fun deleteRoom(room: Room) {
        viewModelScope.launch {
            // Optimistic removal
            _uiState.update { current ->
                current.copy(rooms = current.rooms.filterNot { it == room })
            }
            // Deactivate via API
            if (room.id > 0) {
                roomRepository.changeRoomStatus(room.id, "INACTIVE")
                    .onFailure {
                        // Revert on failure and reload
                        loadRooms()
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
                    _uiEvent.emit(MeetingRoomsEvent.ShowSnackbar(error.message ?: "Failed to add resource"))
                }
        }
    }
}

sealed class MeetingRoomsNavigationEvent {
    object NavigateBack : MeetingRoomsNavigationEvent()
}
