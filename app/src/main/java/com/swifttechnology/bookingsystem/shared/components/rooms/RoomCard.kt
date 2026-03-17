package com.swifttechnology.bookingsystem.shared.components.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomAmenity
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.core.model.defaultRooms

@Composable
fun RoomCard(
    room: Room,
    isEditable: Boolean = false,
    onEditClick: (Room) -> Unit = {},
    onDeleteClick: (Room) -> Unit = {}
) {
    var isEditing by rememberSaveable(room.name) { mutableStateOf(false) }
    var editedName by rememberSaveable(room.name) { mutableStateOf(room.name) }
    var editedCapacity by rememberSaveable(room.name) { mutableStateOf(room.capacity.toString()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(room) {
        if (!isEditing) {
            editedName = room.name
            editedCapacity = room.capacity.toString()
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete room?") },
            text = { Text("This will remove \"${room.name}\" from the list.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteClick(room)
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.customColors.neutral100)
            .border(0.8.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(8.dp))
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isEditable && isEditing) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = room.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.sp,
                        color = if (room.status == RoomStatus.DISABLED) MaterialTheme.customColors.neutral700 else MaterialTheme.customColors.deepBlack,
                        modifier = Modifier.weight(1f)
                    )
                }
                StatusBadge(status = room.status)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (room.timeLabel.isNotEmpty()) {
                    Text(
                        text = room.timeLabel,
                        fontSize = 12.sp,
                        color = MaterialTheme.customColors.textBody,
                        letterSpacing = 0.4.sp,
                        maxLines = 1
                    )
                } else {
                    Spacer(Modifier.width(1.dp))
                }
                if (isEditable && isEditing) {
                    OutlinedTextField(
                        value = editedCapacity,
                        onValueChange = { editedCapacity = it.filter { ch -> ch.isDigit() } },
                        singleLine = true,
                        modifier = Modifier.width(140.dp)
                    )
                } else {
                    Text(
                        text = "Capacity : ${room.capacity}",
                        fontSize = 12.sp,
                        color = MaterialTheme.customColors.textBody,
                        letterSpacing = 0.4.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                room.amenities.forEach { amenity ->
                    AmenityChip(amenity = amenity)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            if (isEditable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.customColors.neutral200)
                            .border(1.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(8.dp))
                            .clickable {
                                if (!isEditing) {
                                    isEditing = true
                                } else {
                                    focusManager.clearFocus()
                                    val capacity = editedCapacity.toIntOrNull() ?: room.capacity
                                    val trimmedName = editedName.trim().ifEmpty { room.name }
                                    isEditing = false
                                    onEditClick(room.copy(name = trimmedName, capacity = capacity))
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Edit,
                            contentDescription = if (isEditing) "Save" else "Edit",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.customColors.deepBlack
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(androidx.compose.ui.graphics.Color(0xFFFF9EA6))
                            .clickable {
                                if (isEditing) {
                                    focusManager.clearFocus()
                                    editedName = room.name
                                    editedCapacity = room.capacity.toString()
                                    isEditing = false
                                } else {
                                    showDeleteConfirm = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Delete,
                            contentDescription = if (isEditing) "Cancel" else "Delete",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.customColors.deepBlack
                        )
                    }
                }
            } else {
                val (btnBg, btnText, btnTextColor) = when (room.status) {
                    RoomStatus.AVAILABLE -> Triple(MaterialTheme.customColors.deepBlack, "Book Now", MaterialTheme.customColors.whitePure)
                    RoomStatus.BOOKED -> Triple(MaterialTheme.customColors.neutral200, "View Schedule", MaterialTheme.customColors.deepBlack)
                    RoomStatus.DISABLED -> Triple(MaterialTheme.customColors.neutral200, "Inquire", MaterialTheme.customColors.deepBlack)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(btnBg)
                        .border(1.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(8.dp))
                        .clickable(enabled = room.status != RoomStatus.DISABLED) {},
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = btnText,
                        fontSize = 14.sp,
                        color = btnTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: RoomStatus) {
    val (bg, label) = when (status) {
        RoomStatus.BOOKED -> MaterialTheme.customColors.yellow100 to "Booked"
        RoomStatus.AVAILABLE -> MaterialTheme.customColors.green100 to "Available"
        RoomStatus.DISABLED -> MaterialTheme.customColors.neutral400 to "Disabled"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 17.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
            color = MaterialTheme.customColors.deepBlack
        )
    }
}

@Composable
private fun AmenityChip(amenity: RoomAmenity) {
    Row(
        modifier = Modifier
            .height(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.customColors.neutral200)
            .padding(horizontal = 8.dp),
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
            color = MaterialTheme.customColors.textBody,
            letterSpacing = 0.4.sp,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomCardPreview() {
    MeetingRoomBookingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RoomCard(room = defaultRooms[1])
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomCardEditablePreview() {
    MeetingRoomBookingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RoomCard(room = defaultRooms[1], isEditable = true)
        }
    }
}
