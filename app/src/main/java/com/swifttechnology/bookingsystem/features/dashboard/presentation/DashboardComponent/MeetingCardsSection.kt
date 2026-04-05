package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO

private val OrangeAccent = Color(0xFFF97316)
private val BlueAccent   = Color(0xFF3B82F6)

data class MeetingCard(
    val title: String,
    val type: String,
    val typeColor: Color,
    val typeBg: Color,
    val time: String,
    val room: String,
    val by: String,
    val participants: Int
)

val meetingCards = listOf(
    MeetingCard(
        "Finance Committee", "Executive",
        Color(0xFF7C3AED), Color(0xFFF3EEFF),
        "Tomorrow, 2:00 PM - 3:00 PM", "Board Room 5A", "Lorem Ipsum", 10
    ),
    MeetingCard(
        "Product Sync", "Internal",
        BlueAccent, Color(0xFFEFF6FF),
        "Tomorrow, 4:00 PM - 5:00 PM", "Conference Room 1A", "Robert Smith", 6
    ),
    MeetingCard(
        "Client Review", "Client",
        OrangeAccent, Color(0xFFFFF7ED),
        "Friday, 10:00 AM - 11:00 AM", "Board Room 5A", "Jane Doe", 8
    )
)

@Composable
fun MeetingCardsSection(meetings: List<BookingResponseDTO> = emptyList()) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(0.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = Spacing.ms)) {
            LazyRow(
                contentPadding        = PaddingValues(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.ms),
                modifier              = Modifier.fillMaxWidth()
            ) {
                if (meetings.isEmpty()) {
                    item {
                        Text(
                            text = "No upcoming meetings",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.customColors.textBody,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(meetings) { meeting ->
                        val card = MeetingCard(
                            title = meeting.meetingTitle ?: "Untitled",
                            type = meeting.meetingType ?: "Unknown",
                            typeColor = if (meeting.meetingType == "CLIENT") OrangeAccent else if (meeting.meetingType == "INTERNAL") BlueAccent else Color(0xFF7C3AED),
                            typeBg = if (meeting.meetingType == "CLIENT") Color(0xFFFFF7ED) else if (meeting.meetingType == "INTERNAL") Color(0xFFEFF6FF) else Color(0xFFF3EEFF),
                            time = "${meeting.date} ${meeting.startTime} - ${meeting.endTime}",
                            room = meeting.roomName ?: "Unknown Room",
                            by = meeting.organizerEmail ?: "System",
                            participants = (meeting.internalParticipantIds?.size ?: 0) + (meeting.externalParticipants?.size ?: 0)
                        )
                        MeetingCardItem(card)
                    }
                }
            }

            if (meetings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                ViewAllButton(onClick = {}, modifier = Modifier.padding(horizontal = Spacing.md))
            }
        }
    }
}

@Composable
private fun MeetingCardItem(card: MeetingCard) {
    Card(
        modifier  = Modifier.width(260.dp),
        shape     = RoundedCornerShape(CornerRadius.lg),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(Elevation.sm)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    card.title,
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(CornerRadius.sm))
                        .background(card.typeBg)
                        .padding(horizontal = Spacing.sm, vertical = 3.dp)
                ) {
                    Text(card.type, style = MaterialTheme.typography.labelSmall, color = card.typeColor)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(card.time,            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.customColors.textBody)
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text("Room: ${card.room}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.customColors.textBody)
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text("By: ${card.by}",     style = MaterialTheme.typography.bodySmall, color = MaterialTheme.customColors.textBody)
            Spacer(modifier = Modifier.height(Spacing.ms))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.People, null, tint = MaterialTheme.customColors.textBody, modifier = Modifier.size(Spacing.ml))
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text("${card.participants} participants", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.customColors.textBody)
                }
                OutlinedButton(
                    onClick        = {},
                    shape          = RoundedCornerShape(CornerRadius.md),
                    contentPadding = PaddingValues(horizontal = Spacing.ms, vertical = Spacing.xs),
                    border         = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier       = Modifier.height(30.dp)
                ) {
                    Text("View Details", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
