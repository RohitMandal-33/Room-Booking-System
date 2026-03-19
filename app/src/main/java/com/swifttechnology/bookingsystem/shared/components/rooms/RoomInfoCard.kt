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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomAmenity
import com.swifttechnology.bookingsystem.core.model.defaultRooms

/**
 * RoomInfoCard — displays room details in the booking form preview section.
 * @param room  The room whose details are shown.
 * @param modifier  Optional modifier for the outer card container.
 * @param onEditClick Optional callback for the edit button.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomInfoCard(
    room: Room,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.customColors.bookRoomInputBackground)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        //  Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color(0xFF7C3AED) // purple-600
                )
                Text(
                    text = "Room Details",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp,
                    color = MaterialTheme.customColors.deepBlack
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Room Name row
        InfoRow(
            label = "Room Name",
            content = {
                Text(
                    text = room.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.1.sp,
                    color = MaterialTheme.customColors.deepBlack
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        // Capacity row
        InfoRow(
            label = "Capacity",
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.customColors.deepBlack
                    )
                    Text(
                        text = "${room.capacity} people",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.1.sp,
                        color = MaterialTheme.customColors.deepBlack
                    )
                }
            }
        )

        Spacer(Modifier.height(20.dp))

        // Divider
        Divider(
            color = MaterialTheme.customColors.neutral300,
            thickness = 0.8.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        //  Available Resources
        Text(
            text = "AVAILABLE RESOURCES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = MaterialTheme.customColors.deepBlack
        )

        Spacer(Modifier.height(12.dp))

        // FlowRow so chips wrap naturally for rooms with many amenities
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            room.amenities.forEach { amenity ->
                ResourceChip(amenity = amenity)
            }
        }
    }
}

// Private helpers

@Composable
private fun InfoRow(
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.customColors.neutral200)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.customColors.textBody,
            letterSpacing = 0.2.sp
        )
        content()
    }
}

/**
 * Amenity chip — slightly larger than the ones in RoomCard to suit the info
 * card context where resources are the focal point.
 */
@Composable
private fun ResourceChip(amenity: RoomAmenity) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.customColors.neutral200)
            .border(0.8.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = amenity.icon,
            contentDescription = amenity.label,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.customColors.textBody
        )
        Text(
            text = amenity.label,
            fontSize = 13.sp,
            color = MaterialTheme.customColors.textBody,
            letterSpacing = 0.3.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5)
@Composable
fun RoomInfoCardPreview() {
    MeetingRoomBookingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RoomInfoCard(room = defaultRooms[1], onEditClick = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F2F5, name = "Empty Amenities")
@Composable
fun RoomInfoCardEmptyAmenitiesPreview() {
    MeetingRoomBookingTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RoomInfoCard(room = defaultRooms[0].copy(amenities = emptyList()))
        }
    }
}