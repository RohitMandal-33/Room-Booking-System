package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation
import com.swifttechnology.bookingsystem.core.designsystem.Primary
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.TextSecondary
import com.swifttechnology.bookingsystem.core.designsystem.customColors

@Composable
fun RoomBookingHeader(
    selectedRoom: String,
    dropdownExpanded: Boolean,
    onDropdownToggle: () -> Unit,
    onDropdownAnchorPositioned: (IntOffset, IntSize) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.BarChart, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(Spacing.ml))
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text("Room Booking Statistics", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.height(Spacing.ms))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Room :", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(modifier = Modifier.width(Spacing.sm))

            OutlinedCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDropdownToggle() }
                    .onGloballyPositioned { coords ->
                        val pos = coords.positionInWindow()
                        onDropdownAnchorPositioned(
                            IntOffset(pos.x.toInt(), pos.y.toInt()),
                            coords.size
                        )
                    },
                shape  = RoundedCornerShape(CornerRadius.md),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.customColors.divider)
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = Spacing.ms, vertical = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        selectedRoom,
                        style    = MaterialTheme.typography.labelLarge,
                        color    = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (dropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint     = MaterialTheme.customColors.textBody,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RoomDropdownOverlay(
    rooms: List<String>,
    selectedRoom: String,
    onRoomSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.width(260.dp),
        shape     = RoundedCornerShape(CornerRadius.md),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(Elevation.lg)
    ) {
        Column(modifier = Modifier.padding(vertical = Spacing.sm)) {
            rooms.forEach { room ->
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .clickable { onRoomSelected(room) }
                        .background(if (room == selectedRoom) MaterialTheme.customColors.primaryLight else Color.Transparent)
                        .padding(horizontal = Spacing.md, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(room, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
