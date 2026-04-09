package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.core.utils.DateTimeUtils
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val OrangeAccent = Color(0xFFF97316)
private val BlueAccent   = Color(0xFF3B82F6)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingCardsSection(
    meetings: List<BookingResponseDTO> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null
) {
    var selectedMeeting by remember { mutableStateOf<BookingResponseDTO?>(null) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(0.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = Spacing.ms)) {

            // ── Section header ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md)
                    .padding(bottom = Spacing.ms),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Upcoming Meetings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isLoading && meetings.isNotEmpty()) {
                    Text(
                        text  = "${meetings.size} meeting${if (meetings.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.customColors.textBody
                    )
                }
            }
            // ─────────────────────────────────────────────────────────────────

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "Could not load meetings: $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                meetings.isEmpty() -> {
                    // ── Empty state ───────────────────────────────────────────
                    Column(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.lg),
                        horizontalAlignment   = Alignment.CenterHorizontally,
                        verticalArrangement   = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Icon(
                            imageVector        = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint               = MaterialTheme.customColors.textBody.copy(alpha = 0.5f),
                            modifier           = Modifier.size(40.dp)
                        )
                        Text(
                            text  = "No upcoming meetings",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.customColors.textBody
                        )
                        Text(
                            text  = "Your scheduled meetings will appear here",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.customColors.textBody.copy(alpha = 0.6f)
                        )
                    }
                    // ─────────────────────────────────────────────────────────
                }

                else -> {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = Spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.ms),
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        items(meetings) { meeting ->
                            val typeColor = meetingTypeColor(meeting.meetingType)
                            val typeBg    = meetingTypeBg(meeting.meetingType)
                            val card = MeetingCard(
                                title        = meeting.meetingTitle ?: "Untitled",
                                type         = formatMeetingType(meeting.meetingType),
                                typeColor    = typeColor,
                                typeBg       = typeBg,
                                time         = formatMeetingTime(meeting.date, meeting.startTime, meeting.endTime),
                                room         = meeting.room?.roomName ?: "Unknown Room",
                                by           = meeting.roomBooker?.email ?: meeting.roomBooker?.firstName ?: "System",
                                participants = (meeting.internalParticipant?.size ?: 0) +
                                              (meeting.externalParticipant?.size ?: 0)
                            )
                            MeetingCardItem(card = card, onViewDetails = { selectedMeeting = meeting })
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    ViewAllButton(onClick = {}, modifier = Modifier.padding(horizontal = Spacing.md))
                }
            }
        }
    }

    selectedMeeting?.let { meeting ->
        MeetingDetailDialog(
            meeting   = meeting,
            onDismiss = { selectedMeeting = null }
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun meetingTypeColor(type: String?): Color = when (type) {
    "CLIENT"    -> OrangeAccent
    "INTERNAL"  -> BlueAccent
    else        -> Color(0xFF7C3AED)  // EXECUTIVE / unknown
}

private fun meetingTypeBg(type: String?): Color = when (type) {
    "CLIENT"    -> Color(0xFFFFF7ED)
    "INTERNAL"  -> Color(0xFFEFF6FF)
    else        -> Color(0xFFF3EEFF)
}

private fun formatMeetingType(type: String?): String = when (type) {
    "CLIENT"    -> "Client"
    "INTERNAL"  -> "Internal"
    "EXECUTIVE" -> "Executive"
    else        -> type ?: "Unknown"
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatMeetingTime(date: String?, startTime: Any?, endTime: Any?): String {
    val dateLabel = formatDateLabel(date)
    val startStr  = DateTimeUtils.formatTimeDisplay(startTime)
    val endStr    = DateTimeUtils.formatTimeDisplay(endTime)

    return when {
        startStr.isNotEmpty() && endStr.isNotEmpty() -> "$dateLabel, $startStr – $endStr"
        startStr.isNotEmpty()                        -> "$dateLabel, $startStr"
        else                                         -> dateLabel
    }
}

/**
 * Converts a yyyy-MM-dd date string to a human-readable label:
 *  - "Today"
 *  - "Tomorrow"
 *  - "Apr 9" (within the next 7 days)
 *  - "Apr 9, 2026" (further out)
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun formatDateLabel(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return "Upcoming"
    return try {
        val date  = LocalDate.parse(dateStr)
        val today = LocalDate.now()
        when (date) {
            today            -> "Today"
            today.plusDays(1) -> "Tomorrow"
            else -> {
                val pattern = if (date.year == today.year) "MMM d" else "MMM d, yyyy"
                date.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
            }
        }
    } catch (e: Exception) {
        dateStr
    }
}

// ── UI Components ─────────────────────────────────────────────────────────────

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

@Composable
private fun MeetingCardItem(card: MeetingCard, onViewDetails: () -> Unit) {
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
                    fontWeight = FontWeight.SemiBold,
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint               = MaterialTheme.customColors.textBody,
                    modifier           = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    card.time,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.customColors.textBody,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Outlined.Place,
                    contentDescription = null,
                    tint               = MaterialTheme.customColors.textBody,
                    modifier           = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    card.room,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.customColors.textBody,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint               = MaterialTheme.customColors.textBody,
                    modifier           = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    card.by,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.customColors.textBody,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(Spacing.ms))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.People,
                        null,
                        tint     = MaterialTheme.customColors.textBody,
                        modifier = Modifier.size(Spacing.ml)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        "${card.participants} participant${if (card.participants != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.customColors.textBody
                    )
                }
                OutlinedButton(
                    onClick        = onViewDetails,
                    shape          = RoundedCornerShape(CornerRadius.md),
                    contentPadding = PaddingValues(horizontal = Spacing.ms, vertical = Spacing.xs),
                    border         = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier       = Modifier.height(30.dp)
                ) {
                    Text(
                        "View Details",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MeetingDetailDialog(
    meeting: BookingResponseDTO,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape    = RoundedCornerShape(16.dp),
            color    = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "Meeting Details",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title + type badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val color = meetingTypeColor(meeting.meetingType)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text       = meeting.meetingTitle ?: "Untitled",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "Reserved · ${formatMeetingType(meeting.meetingType)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val participantsCount =
                    (meeting.internalParticipant?.size ?: 0) +
                    (meeting.externalParticipant?.size ?: 0)

                DetailItem(
                    icon     = Icons.Outlined.Schedule,
                    title    = formatDateLabel(meeting.date),
                    subtitle = run {
                        val start = DateTimeUtils.formatTimeDisplay(meeting.startTime)
                        val end   = DateTimeUtils.formatTimeDisplay(meeting.endTime)
                        if (start.isNotEmpty() && end.isNotEmpty()) "$start – $end" else "Time not set"
                    }
                )

                DetailItem(
                    icon     = Icons.Outlined.Place,
                    title    = meeting.room?.roomName?.takeIf { it.isNotEmpty() } ?: "Unassigned",
                    subtitle = "Meeting Room"
                )

                DetailItem(
                    icon     = Icons.Outlined.AccountCircle,
                    title    = "Organized by",
                    subtitle = meeting.roomBooker?.email?.takeIf { it.isNotEmpty() } ?: "Unknown"
                )

                ExpandableAttendeesItem(meeting = meeting)

                if (!meeting.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text  = meeting.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.customColors.textBody
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick  = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape    = RoundedCornerShape(8.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.customColors.textBody,
            modifier           = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title,    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall,  color = MaterialTheme.customColors.textBody)
        }
    }
}

@Composable
private fun ExpandableAttendeesItem(meeting: BookingResponseDTO) {
    var expanded by remember { mutableStateOf(false) }

    val internal = meeting.internalParticipant ?: emptyList()
    val external = meeting.externalParticipant ?: emptyList()
    val total = internal.size + external.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { if (total > 0) expanded = !expanded },
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.People,
                    contentDescription = null,
                    tint = MaterialTheme.customColors.textBody,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Attendees", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text(
                        text = "$total ${if (total == 1) "person" else "people"}",
                        style = MaterialTheme.typography.bodySmall,  
                        color = MaterialTheme.customColors.textBody
                    )
                }
            }
            if (total > 0) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.customColors.textBody,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 8.dp)
            ) {
                internal.forEach { p ->
                    val name = listOfNotNull(p.firstName, p.lastName).joinToString(" ").takeIf { it.isNotBlank() } ?: p.email ?: "Unknown"
                    Text(
                        text = "• $name (Internal)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.customColors.textBody,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                external.forEach { p ->
                    Text(
                        text = "• ${p.name} (External)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.customColors.textBody,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
