package com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold

private val editScreenAmenities = listOf(
    RoomAmenity("PROJECTOR", Icons.Outlined.Slideshow),
    RoomAmenity("WHITEBOARD", Icons.Outlined.BorderColor),
    RoomAmenity("TV", Icons.Outlined.Videocam),
    RoomAmenity("WIFI", Icons.Outlined.Wifi)
)

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Room not found",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    var editedName by rememberSaveable(roomName) { mutableStateOf(room.name) }
    var editedCapacity by rememberSaveable(roomName) { mutableStateOf(room.capacity.toString()) }
    var showCustomAmenityDialog by rememberSaveable { mutableStateOf(false) }
    var customAmenityName by rememberSaveable { mutableStateOf("") }
    val editedAmenities = remember(roomName) { room.amenities.toMutableStateList() }

    LaunchedEffect(room) {
        editedName = room.name
        editedCapacity = room.capacity.toString()
        editedAmenities.clear()
        editedAmenities.addAll(room.amenities)
    }

    MainScaffold(
        sidebarItems = uiState.sidebarItems,
        selectedItem = uiState.selectedItem,
        title = "Edit Mode",
        onSidebarItemSelected = { item ->
            viewModel.onSidebarItemSelected(item)
            onNavigate(item.route)
        },
        onLogout = onLogout
    ) {
        if (showCustomAmenityDialog) {
            AlertDialog(
                onDismissRequest = { showCustomAmenityDialog = false },
                title = { Text(text = "Add Custom Amenity") },
                text = {
                    OutlinedTextField(
                        value = customAmenityName,
                        onValueChange = { customAmenityName = it },
                        label = { Text("Amenity Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (customAmenityName.isNotBlank()) {
                                editedAmenities.add(RoomAmenity(customAmenityName.trim(), Icons.Outlined.CheckCircle))
                                customAmenityName = ""
                                showCustomAmenityDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomAmenityDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                label = { Text("Capacity") }
            )

            Text(
                text = "Amenities",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.deepBlack
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                editScreenAmenities.forEach { amenity ->
                    val isSelected = editedAmenities.any { it.label == amenity.label }
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
                                if (isSelected) {
                                    editedAmenities.removeIf { it.label == amenity.label }
                                } else {
                                    editedAmenities.add(amenity)
                                }
                            }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = amenity.icon,
                            contentDescription = amenity.label,
                            modifier = Modifier.size(14.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.customColors.textBody
                        )
                        Text(
                            text = amenity.label,
                            fontSize = 12.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.customColors.textBody,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }

                // Show custom amenities
                val customAmenities = editedAmenities.filter { sel -> editScreenAmenities.none { it.label == sel.label } }
                customAmenities.forEach { amenity ->
                    Row(
                        modifier = Modifier
                            .height(28.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                            .clickable { editedAmenities.removeIf { it.label == amenity.label } }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = amenity.icon,
                            contentDescription = amenity.label,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = amenity.label,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Plus icon for custom amenities
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(MaterialTheme.customColors.neutral200, RoundedCornerShape(6.dp))
                        .border(1.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(6.dp))
                        .clickable { showCustomAmenityDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add custom amenity",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.customColors.textBody
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                        val trimmedName = editedName.trim().ifEmpty { room.name }
                        val currentAmenities = editedAmenities.toList()
                        viewModel.updateRoom(
                            original = room,
                            updated = room.copy(
                                name = trimmedName,
                                capacity = capacity,
                                amenities = currentAmenities,
                                resources = currentAmenities.map { it.label }
                            )
                        )
                        onBack()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save changes", textAlign = TextAlign.Center)
                }
            }
        }
    }
}
