package com.swifttechnology.bookingsystem.features.meetingrooms.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.rooms.RoomCard
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import kotlinx.coroutines.launch

import androidx.compose.runtime.LaunchedEffect
import com.swifttechnology.bookingsystem.core.model.Room

@Composable
fun MeetingRoomsScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    onOpenRoomEdit: (String) -> Unit,
    onNavigateToAddRoom: () -> Unit,
    onBookClick: (Room) -> Unit,
    isEditable: Boolean = false,
    viewModel: MeetingRoomsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadRooms()
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MeetingRoomsEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading && uiState.rooms.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.filteredRooms.isEmpty()) {
            val emptyText = if (uiState.errorMessage != null) {
                "Error: ${uiState.errorMessage}"
            } else if (uiState.searchQuery.isNotBlank()) {
                "No rooms match \"${uiState.searchQuery}\""
            } else {
                "No meeting rooms found."
            }
            androidx.compose.material3.Text(
                text = emptyText,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
                color = if (uiState.errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp), // extra bottom pad so last card isn't hidden behind FAB
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(uiState.filteredRooms, key = { "${it.id}-${it.name}" }) { room ->
                    RoomCard(
                        room = room,
                        isEditable = isEditable,
                        onEditClick = { roomToEdit ->
                            onOpenRoomEdit(roomToEdit.name)
                        },
                        onDeleteClick = { roomToDelete ->
                            viewModel.deleteRoom(roomToDelete)
                        },
                        onBookClick = onBookClick
                    )
                }
            }
        }

        AddFab(
            onClick = { onNavigateToAddRoom() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun AddFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Room"
        )
    }
}
