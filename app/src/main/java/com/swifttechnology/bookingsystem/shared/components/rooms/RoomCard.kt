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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomAmenity
import com.swifttechnology.bookingsystem.core.model.RoomStatus

@Composable
fun RoomCard(room: Room) {
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
                Text(
                    text = room.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp,
                    color = if (room.status == RoomStatus.DISABLED) MaterialTheme.customColors.neutral700 else MaterialTheme.customColors.deepBlack,
                    modifier = Modifier.weight(1f)
                )
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
                Text(
                    text = "Capacity : ${room.capacity}",
                    fontSize = 12.sp,
                    color = MaterialTheme.customColors.textBody,
                    letterSpacing = 0.4.sp
                )
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

