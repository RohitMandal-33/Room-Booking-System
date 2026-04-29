package com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.core.model.RoomAmenity
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomsViewModel
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomsNavigationEvent
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditRoomCardScreen(
    roomName: String,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: MeetingRoomsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val room = uiState.rooms.firstOrNull { it.name == roomName }

    if (room == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var editedName by rememberSaveable(room.id) { mutableStateOf(room.name) }
    var editedCapacity by rememberSaveable(room.id) { mutableStateOf(room.capacity.toString()) }
    val selectedResourceIds = remember(room.id) { 
        room.resourceIds.toMutableStateList() 
    }

    MainScaffold(
        sidebarItems = uiState.sidebarItems,
        selectedItem = uiState.selectedItem,
        title = "Edit Room",
        onSidebarItemSelected = { item ->
            viewModel.onSidebarItemSelected(item)
            onNavigate(item.route)
        },
        onLogout = onLogout
    ) {
        LaunchedEffect(Unit) {
            viewModel.navigationEvents.collect { event ->
                if (event is MeetingRoomsNavigationEvent.NavigateBack) {
                    kotlinx.coroutines.delay(100)
                    onBack()
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit Room",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.deepBlack
            )

            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Room name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.customColors.neutral300,
                    unfocusedBorderColor = MaterialTheme.customColors.neutral300,
                    cursorColor = MaterialTheme.customColors.deepBlack,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            OutlinedTextField(
                value = editedCapacity,
                onValueChange = { editedCapacity = it.filter { ch -> ch.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Capacity") },
                leadingIcon = { Icon(Icons.Outlined.Person, null, modifier = Modifier.size(18.dp)) }
            )

            Text(
                text = "Amenities",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.deepBlack
            )

            if (uiState.allResources.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.allResources.forEach { resource ->
                        val isSelected = selectedResourceIds.contains(resource.id)
                        val amenity = RoomAmenity.fromResourceString(resource.name ?: "")
                        
                        Row(
                            modifier = Modifier
                                .height(28.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.customColors.neutral200,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.customColors.neutral300,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    if (isSelected) selectedResourceIds.remove(resource.id)
                                    else resource.id?.let { selectedResourceIds.add(it) }
                                }
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = amenity?.icon ?: Icons.Outlined.CheckCircle,
                                contentDescription = resource.name,
                                modifier = Modifier.size(14.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.customColors.textBody
                            )
                            Text(
                                text = resource.name ?: "Unknown",
                                fontSize = 12.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.customColors.textBody,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }

                    // Add new amenity button
                    var showAddDialog by remember { mutableStateOf(false) }
                    var newAmenityName by remember { mutableStateOf("") }

                    if (showAddDialog) {
                        AlertDialog(
                            onDismissRequest = { showAddDialog = false },
                            title = { Text("Add Amenity") },
                            text = {
                                OutlinedTextField(
                                    value = newAmenityName,
                                    onValueChange = { newAmenityName = it },
                                    label = { Text("Amenity Name") },
                                    singleLine = true
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    if (newAmenityName.isNotBlank()) {
                                        viewModel.addResource(newAmenityName.trim())
                                        newAmenityName = ""
                                        showAddDialog = false
                                    }
                                }) {
                                    Text("Add")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.customColors.neutral200, RoundedCornerShape(6.dp))
                            .border(1.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(6.dp))
                            .clickable { showAddDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add Amenity",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.customColors.textBody
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onBack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        val capacity = editedCapacity.toIntOrNull() ?: room.capacity
                        val trimmedName = editedName.trim()
                        if (trimmedName.isNotEmpty()) {
                            viewModel.updateRoom(
                                original = room,
                                updated = room.copy(
                                    name = trimmedName,
                                    capacity = capacity,
                                    resourceIds = selectedResourceIds.toList()
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Changes", textAlign = TextAlign.Center)
                }
            }
        }
    }
}
