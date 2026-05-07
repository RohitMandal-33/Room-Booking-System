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
    onToggleStatusClick: (Room) -> Unit = {},
    onBookClick: (Room) -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    val isDisabled = room.status == RoomStatus.INACTIVE || room.status == RoomStatus.DISABLED
    var showToggleConfirm by remember { mutableStateOf(false) }

    if (showToggleConfirm) {
        AlertDialog(
            onDismissRequest = { showToggleConfirm = false },
            title = { Text(if (isDisabled) "Enable Room" else "Disable Room") },
            text = {
                Text(
                    if (isDisabled)
                        "Are you sure you want to enable \"${room.name}\"? It will become available for booking."
                    else
                        "Are you sure you want to disable \"${room.name}\"? It will be marked as under maintenance."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onToggleStatusClick(room)
                    showToggleConfirm = false
                }) {
                    Text(
                        if (isDisabled) "Enable" else "Disable",
                        color = if (isDisabled) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showToggleConfirm = false }) {
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

                    // Enable / Disable toggle button
                    val toggleBg = if (isDisabled) Color(0xFFC8E6C9) else Color(0xFFFF9EA6)
                    val toggleIcon = if (isDisabled) Icons.Outlined.CheckCircle else Icons.Outlined.Block
                    val toggleDesc = if (isDisabled) "Enable Room" else "Disable Room"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(CornerRadius.md))
                            .background(toggleBg)
                            .clickable { showToggleConfirm = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = toggleIcon,
                            contentDescription = toggleDesc,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.customColors.deepBlack
                        )
                    }
                }
            } else {
                // Normal mode action button
                when {
                    isDisabled -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF3E0))
                                .border(
                                    1.dp,
                                    Color(0xFFFFCC80),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Construction,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFFE65100)
                                )
                                Text(
                                    text = "Not Available",
                                    fontSize = 13.sp,
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                    room.status == RoomStatus.BOOKED -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.customColors.neutral200)
                                .border(
                                    1.dp,
                                    MaterialTheme.customColors.neutral300,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onBookClick(room) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "View Schedule",
                                fontSize = 14.sp,
                                color = MaterialTheme.customColors.deepBlack,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    else -> {
                        // AVAILABLE / ACTIVE
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.customColors.deepBlack)
                                .border(
                                    1.dp,
                                    MaterialTheme.customColors.neutral300,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onBookClick(room) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Book Now",
                                fontSize = 14.sp,
                                color = MaterialTheme.customColors.whitePure,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
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
