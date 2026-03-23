package com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomComponets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
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

private val addScreenAmenities = listOf(
    RoomAmenity("PROJECTOR", Icons.Outlined.Slideshow),
    RoomAmenity("WHITEBOARD", Icons.Outlined.BorderColor),
    RoomAmenity("TV", Icons.Outlined.Videocam),
    RoomAmenity("WIFI", Icons.Outlined.Wifi)
)

@Composable
fun AddRoomScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: MeetingRoomsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var newName by rememberSaveable { mutableStateOf("") }
    var newCapacity by rememberSaveable { mutableStateOf("") }
    var showAddAmenityDialog by rememberSaveable { mutableStateOf(false) }
    val selectedAmenities = remember { mutableListOf<RoomAmenity>().toMutableStateList() }

    MainScaffold(
        sidebarItems = uiState.sidebarItems,
        selectedItem = uiState.selectedItem,
        searchQuery = uiState.searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSidebarItemSelected = { item ->
            viewModel.onSidebarItemSelected(item)
            onNavigate(item.route)
        },
        onLogout = onLogout
    ) {
        if (showAddAmenityDialog) {
            val available = addScreenAmenities.filter { preset -> selectedAmenities.none { it.label == preset.label } }

            AlertDialog(
                onDismissRequest = { showAddAmenityDialog = false },
                title = {
                    Text(
                        "Add Amenity",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        available.forEach { amenity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.customColors.neutral200, RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedAmenities.add(amenity)
                                        showAddAmenityDialog = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = amenity.icon,
                                    contentDescription = amenity.label,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.customColors.textBody
                                )
                                Text(
                                    text = amenity.label,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.customColors.deepBlack
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAddAmenityDialog = false }) {
                        Text("Close")
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
                text = "Add Room",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.deepBlack
            )

            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
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
                value = newCapacity,
                onValueChange = { newCapacity = it.filter { ch -> ch.isDigit() } },
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
                selectedAmenities.forEach { amenity ->
                    Row(
                        modifier = Modifier
                            .height(28.dp)
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                            .background(MaterialTheme.customColors.neutral200, RoundedCornerShape(4.dp))
                            .padding(start = 8.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = amenity.icon,
                            contentDescription = amenity.label,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.customColors.textBody
                        )
                        Text(
                            text = amenity.label,
                            fontSize = 12.sp,
                            color = MaterialTheme.customColors.textBody
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { selectedAmenities.remove(amenity) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Remove",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.customColors.textBody
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .height(28.dp)
                        .border(1.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(4.dp))
                        .clickable { showAddAmenityDialog = true }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add amenity",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.customColors.textBody
                    )
                    Text(
                        text = "Add custom amenity",
                        fontSize = 12.sp,
                        color = MaterialTheme.customColors.textBody
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
                        val capacity = newCapacity.toIntOrNull() ?: 10
                        val trimmedName = newName.trim()
                        if (trimmedName.isNotEmpty()) {
                            viewModel.addRoom(
                                name = trimmedName,
                                capacity = capacity,
                                amenities = selectedAmenities.toList()
                            )
                            onBack()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Room", textAlign = TextAlign.Center)
                }
            }
        }
    }
}
