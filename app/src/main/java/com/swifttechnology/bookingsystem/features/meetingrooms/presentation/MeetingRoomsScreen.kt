package com.swifttechnology.bookingsystem.features.meetingrooms.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.rooms.RoomCard
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import kotlinx.coroutines.launch

@Composable
fun MeetingRoomsScreen(
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: MeetingRoomsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    MainScaffold(
        sidebarItems = uiState.sidebarItems,
        selectedItem = uiState.selectedItem,
        searchQuery = uiState.searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSidebarItemSelected = { item ->
            if (item.route == ScreenRoutes.BOOK_ROOM) {
                onNavigate(ScreenRoutes.BOOK_ROOM)
            } else {
                viewModel.onSidebarItemSelected(item)
                onNavigate(item.route)
            }
        },
        onLogout = {
            scope.launch {
                viewModel.logout()
                onLogout()
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(uiState.filteredRooms) { room ->
                RoomCard(
                    room = room,
                    isEditable = true,
                    onEditClick = { updatedRoom ->
                        viewModel.updateRoom(original = room, updated = updatedRoom)
                    },
                    onDeleteClick = { roomToDelete ->
                        viewModel.deleteRoom(roomToDelete)
                    }
                )
            }
        }
    }
}

