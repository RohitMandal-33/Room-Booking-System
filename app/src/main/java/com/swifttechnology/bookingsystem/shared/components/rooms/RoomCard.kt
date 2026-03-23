package com.swifttechnology.bookingsystem.shared.components.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
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
import com.swifttechnology.bookingsystem.core.model.defaultAmenities
import com.swifttechnology.bookingsystem.core.model.defaultRooms

private val allAvailableAmenities = listOf(
    RoomAmenity("PROJECTOR", Icons.Outlined.Slideshow),
    RoomAmenity("WHITEBOARD", Icons.Outlined.BorderColor),
    RoomAmenity("TV", Icons.Outlined.Videocam),
    RoomAmenity("WIFI", Icons.Outlined.Wifi)
)

@OptIn(ExperimentalLayoutApi::class)
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
    var showAddAmenityDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Mutable snapshot list for amenities being edited
    val editedAmenities = remember(room.name) { room.amenities.toMutableStateList() }

    LaunchedEffect(room) {
        if (!isEditing) {
            editedName = room.name
            editedCapacity = room.capacity.toString()
            editedAmenities.clear()
            editedAmenities.addAll(room.amenities)
        }
    }

    // Reset editing state when top-bar edit mode is turned off
    LaunchedEffect(isEditable) {
        if (!isEditable && isEditing) {
            isEditing = false
            editedName = room.name
            editedCapacity = room.capacity.toString()
            editedAmenities.clear()
            editedAmenities.addAll(room.amenities)
        }
    }

    // Add amenity dialog
    if (showAddAmenityDialog) {
        val currentLabels = editedAmenities.map { it.label }.toSet()
        val available = allAvailableAmenities.filter { it.label !in currentLabels }

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
                if (available.isEmpty()) {
                    Text(
                        "All amenities have been added.",
                        fontSize = 14.sp,
                        color = MaterialTheme.customColors.textBody
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        available.forEach { amenity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.customColors.neutral200)
                                    .clickable {
                                        editedAmenities.add(amenity)
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
            .fillMaxWidth()
            .then(
                if (isEditable && isEditing) Modifier.wrapContentHeight()
                else Modifier.height(216.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.customColors.neutral100)
            .border(0.8.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(8.dp))
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            //  Room name + status badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isEditable && isEditing) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.1.sp,
                            color = MaterialTheme.customColors.deepBlack
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.customColors.neutral300,
                            unfocusedBorderColor = MaterialTheme.customColors.neutral300,
                            cursorColor = MaterialTheme.customColors.deepBlack,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                } else {
                    Text(
                        text = room.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.sp,
                        color = if (room.status == RoomStatus.DISABLED)
                            MaterialTheme.customColors.neutral700
                        else MaterialTheme.customColors.deepBlack,
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
                // Left side: time label or empty spacer
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

                // Right side: capacity
                if (isEditable && isEditing) {
                    OutlinedTextField(
                        value = editedCapacity,
                        onValueChange = { editedCapacity = it.filter { ch -> ch.isDigit() } },
                        singleLine = true,
                        modifier = Modifier.width(140.dp),
                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.customColors.deepBlack
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Capacity",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.customColors.textBody
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.customColors.neutral300,
                            unfocusedBorderColor = MaterialTheme.customColors.neutral300,
                            cursorColor = MaterialTheme.customColors.deepBlack,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(6.dp),
                        label = {
                            Text(
                                "Capacity",
                                fontSize = 10.sp,
                                color = MaterialTheme.customColors.textBody
                            )
                        }
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Capacity",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.customColors.textBody
                        )
                        Text(
                            text = "Capacity : ${room.capacity}",
                            fontSize = 12.sp,
                            color = MaterialTheme.customColors.textBody,
                            letterSpacing = 0.4.sp
                        )
                    }
                }
            }

            // Amenity chips
            if (isEditable && isEditing) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    editedAmenities.forEach { amenity ->
                        EditableAmenityChip(
                            amenity = amenity,
                            onRemove = { editedAmenities.remove(amenity) }
                        )
                    }
                    AddAmenityChip(onClick = { showAddAmenityDialog = true })
                }
            } else {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    room.amenities.forEach { amenity ->
                        AmenityChip(amenity = amenity)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        //Action buttons
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
                    // Edit / Save button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.customColors.neutral200)
                            .border(
                                1.dp,
                                MaterialTheme.customColors.neutral300,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onEditClick(room) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = if (isEditing) "Save" else "Edit",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.customColors.deepBlack
                        )
                    }

                    // Delete / Cancel button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF9EA6))
                            .clickable {
                                if (isEditing) {
                                    focusManager.clearFocus()
                                    editedName = room.name
                                    editedCapacity = room.capacity.toString()
                                    editedAmenities.clear()
                                    editedAmenities.addAll(room.amenities)
                                    isEditing = false
                                } else {
                                    showDeleteConfirm = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = if (isEditing) "Cancel" else "Delete",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.customColors.deepBlack
                        )
                    }
                }
            } else {
                // Normal mode action button
                val (btnBg, btnText, btnTextColor) = when (room.status) {
                    RoomStatus.AVAILABLE, RoomStatus.ACTIVE -> Triple(
                        MaterialTheme.customColors.deepBlack,
                        "Book Now",
                        MaterialTheme.customColors.whitePure
                    )
                    RoomStatus.BOOKED -> Triple(
                        MaterialTheme.customColors.neutral200,
                        "View Schedule",
                        MaterialTheme.customColors.deepBlack
                    )
                    RoomStatus.DISABLED, RoomStatus.INACTIVE -> Triple(
                        MaterialTheme.customColors.neutral200,
                        "Inquire",
                        MaterialTheme.customColors.deepBlack
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(btnBg)
                        .border(
                            1.dp,
                            MaterialTheme.customColors.neutral300,
                            RoundedCornerShape(8.dp)
                        )
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
        RoomStatus.AVAILABLE, RoomStatus.ACTIVE -> MaterialTheme.customColors.green100 to "Available"
        RoomStatus.DISABLED, RoomStatus.INACTIVE -> MaterialTheme.customColors.neutral400 to "Disabled"
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

/** Read-only amenity chip (normal view). */
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

/** Editable amenity chip with a remove (×) button. */
@Composable
private fun EditableAmenityChip(
    amenity: RoomAmenity,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.customColors.neutral200)
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
            color = MaterialTheme.customColors.textBody,
            letterSpacing = 0.4.sp,
            maxLines = 1
        )
        // Remove button
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Remove ${amenity.label}",
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.customColors.textBody
            )
        }
    }
}

/** "+ Add" chip button to add a new amenity. */
@Composable
private fun AddAmenityChip(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 0.8.dp,
                color = MaterialTheme.customColors.neutral300,
                shape = RoundedCornerShape(4.dp)
            )
            .background(Color.Transparent)
            .clickable { onClick() }
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
            text = "Add",
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