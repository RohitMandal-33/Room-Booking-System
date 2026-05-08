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
import androidx.compose.ui.res.painterResource
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
import com.swifttechnology.bookingsystem.R
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.MeetingDetailBottomSheet
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.swifttechnology.bookingsystem.core.utils.ColorUtils

private val OrangeAccent = Color(0xFFF97316)
private val BlueAccent   = Color(0xFF3B82F6)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingCardsSection(
    meetings: List<BookingResponseDTO> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    onEditMeeting: (BookingResponseDTO, String) -> Unit = { _, _ -> }
) {
    var selectedMeeting by remember { mutableStateOf<BookingResponseDTO?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.ms)
    ) {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.ms),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text  = "Upcoming Meetings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
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
                    //      Empty state                                                                                       
                    Column(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.lg),
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
                    //                                                                                                                   
                }

                else -> {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.ms),
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        items(meetings) { meeting ->
                            val colorCode = meeting.meetingTypeColorCode
                            val typeColor = ColorUtils.parseColor(colorCode)
                            val typeBg    = ColorUtils.getLightBackgroundColor(typeColor, androidx.compose.foundation.isSystemInDarkTheme())
                            
                            val card = MeetingCard(
                                title        = meeting.meetingTitle ?: "Untitled",
                                type         = formatMeetingType(meeting.meetingType),
                                typeColor    = typeColor,
                                typeBg       = typeBg,
                                time         = formatMeetingTime(meeting.date, meeting.startTime, meeting.endTime),
                                room         = meeting.room?.roomName ?: "Unknown Room",
                                by           = meeting.roomBooker?.email ?: meeting.roomBooker?.firstName ?: "System",
                                participants = (meeting.internalParticipant?.size ?: 0) +
                                              (meeting.externalParticipant?.size ?: 0),
                                isRecurring  = meeting.isRecurring()
                            )
                            MeetingCardItem(card = card, onViewDetails = { selectedMeeting = meeting })
                        }
                    }

//                    Spacer(modifier = Modifier.height(10.dp))
//                    androidx.compose.material3.TextButton(
//                        onClick = {},
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(
//                            text = "View All",
//                            style = MaterialTheme.typography.labelLarge,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
                }
            }
    }

    selectedMeeting?.let { meeting ->
        MeetingDetailBottomSheet(
            event = meeting.toMeetingEvent(),
            onDismiss = { selectedMeeting = null },
            onEdit = { scope ->
                onEditMeeting(meeting, scope)
                selectedMeeting = null
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun BookingResponseDTO.toMeetingEvent(): MeetingEvent {
    val dateObj = runCatching { LocalDate.parse(date ?: startDate) }.getOrNull() ?: LocalDate.now()
    val startTimeObj = DateTimeUtils.parseLocalTime(startTimeString) ?: DateTimeUtils.parseLocalTime(startTime) ?: LocalTime.MIN
    val endTimeObj = DateTimeUtils.parseLocalTime(endTimeString) ?: DateTimeUtils.parseLocalTime(endTime) ?: LocalTime.MAX

    val typeColor = ColorUtils.parseColor(meetingTypeColorCode)
    
    return MeetingEvent(
        id = (id ?: meetingId ?: 0L).toInt(),
        meetingId = meetingId ?: id,
        title = meetingTitle ?: "Untitled",
        date = dateObj,
        startTime = startTimeObj,
        endTime = endTimeObj,
        color = typeColor,
        backendColor = typeColor,
        description = description ?: "",
        meetingRoom = room?.roomName ?: roomName ?: "Unknown Room",
        meetingType = meetingType ?: "",
        internalParticipants = internalParticipant ?: emptyList(),
        externalParticipants = externalParticipant ?: emptyList(),
        createdBy = roomBooker?.email ?: roomBooker?.firstName ?: "System",
        recurrenceId = recurrenceId,
        recurrenceType = recurrenceType,
        repeats = !recurrenceId.isNullOrBlank() && recurrenceType != "NONE",
        meetingTypeId = meetingTypeId
    )
}

//      Helpers                                                                                                                                       

// Helper functions for static types removed in favor of ColorUtils.parseColor(meetingTypeColorCode)

private fun formatMeetingType(type: String?): String {
    return type?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Meeting"
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

private fun formatRecurrenceType(type: String?): String {
    return when (type?.uppercase()) {
        "DAILY" -> "Repeats daily"
        "WEEKDAY" -> "Repeats on weekdays"
        "WEEKLY" -> "Repeats weekly"
        "MONTHLY" -> "Repeats monthly"
        "YEARLY" -> "Repeats yearly"
        "CUSTOM" -> "Custom recurrence"
        else -> "Does not repeat"
    }
}

//      UI Components                                                                                                                           

data class MeetingCard(
    val title: String,
    val type: String,
    val typeColor: Color,
    val typeBg: Color,
    val time: String,
    val room: String,
    val by: String,
    val participants: Int,
    val isRecurring: Boolean
)

@Composable
private fun MeetingCardItem(card: MeetingCard, onViewDetails: () -> Unit) {
    Card(
        modifier  = Modifier.width(260.dp),
        shape     = RoundedCornerShape(CornerRadius.lg),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (card.isRecurring) {
                        Icon(
                            painter = painterResource(R.drawable.ic_repeat),
                            contentDescription = "Recurring meeting",
                            tint = MaterialTheme.customColors.textBody,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        card.title,
                        style    = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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

// MeetingDetailDialog removed as it's replaced by MeetingDetailBottomSheet from calendar components


private fun BookingResponseDTO.isRecurring(): Boolean {
    return !recurrenceId.isNullOrBlank() && !recurrenceType.isNullOrBlank() && recurrenceType != "NONE"
}






