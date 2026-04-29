package com.swifttechnology.bookingsystem.shared.components.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomAmenity
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.core.model.defaultRooms

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomCard(
    room: Room,
    isEditable: Boolean = false,
    onEditClick: (Room) -> Unit = {},
    onDeleteClick: (Room) -> Unit = {},
    onBookClick: (Room) -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Room") },
            text = { Text("Are you sure you want to delete this room?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick(room)
                    showDeleteConfirm = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(216.dp)
            .pointerInput(isEditable) {
                detectTapGestures(
                    onLongPress = {
                        if (!isEditable) {
                            onLongPress()
                        }
                    }
                )
            }
            .clip(RoundedCornerShape(CornerRadius.md))
            .background(MaterialTheme.customColors.neutral100)
            .border(0.8.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(CornerRadius.md))
            .padding(vertical = Spacing.md),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {

            //  Room name + status badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (room.status == RoomStatus.DISABLED)
                        MaterialTheme.customColors.neutral700
                    else MaterialTheme.customColors.deepBlack,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = room.status)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: time label
                Text(
                    text = room.timeLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.customColors.textBody,
                    maxLines = 1
                )

                // Right side: capacity
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
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.customColors.textBody
                    )
                }
            }

            // Amenity chips
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                room.amenities.forEach { amenity ->
                    AmenityChip(amenity = amenity)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        //Action buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
        ) {
            if (isEditable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Edit button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(CornerRadius.sm))
                            .background(MaterialTheme.customColors.neutral200)
                            .border(
                                1.dp,
                                MaterialTheme.customColors.neutral300,
                                RoundedCornerShape(CornerRadius.sm)
                            )
                            .clickable { onEditClick(room) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.customColors.deepBlack
                        )
                    }

                    // Delete button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(CornerRadius.md))
                            .background(Color(0xFFFF9EA6))
                            .clickable { showDeleteConfirm = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
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
                        .clickable(enabled = room.status != RoomStatus.DISABLED) { onBookClick(room) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = btnText,
                        fontSize = 14.sp,
                        color = btnTextColor,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
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
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.customColors.deepBlack
        )
    }
}

@Composable
fun AmenityChip(amenity: RoomAmenity) {
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
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.customColors.textBody,
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
