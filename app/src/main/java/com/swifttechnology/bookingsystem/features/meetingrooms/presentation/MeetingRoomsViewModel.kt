package com.swifttechnology.bookingsystem.features.meetingrooms.presentation

import androidx.lifecycle.ViewModel
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.defaultRooms
import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class MeetingRoomsViewModel @Inject constructor(
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MeetingRoomsUiState(rooms = defaultRooms)
    )
    val uiState: StateFlow<MeetingRoomsUiState> = _uiState.asStateFlow()

    fun updateRoom(original: Room, updated: Room) {
        _uiState.update { current ->
            current.copy(
                rooms = current.rooms.map { if (it == original) updated else it }
            )
        }
    }

    fun deleteRoom(room: Room) {
        _uiState.update { current ->
            current.copy(rooms = current.rooms.filterNot { it == room })
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
        tokenStorage.clear()
    }
}

