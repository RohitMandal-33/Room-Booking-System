package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import java.time.format.DateTimeFormatter

private val PurplePrimary = Color(0xFF6C3EE8)
private val PurpleSoft = Color(0xFFF3EEFF)
private val SurfaceTint = Color(0xFFF8F9FC)
private val BorderSoft = Color(0xFFE8EAF1)
private val TextMuted = Color(0xFF667085)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingDetailBottomSheet(
    event: MeetingEvent,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val meetingTypeUpper = event.meetingType.uppercase()

    val typeColor = when (meetingTypeUpper) {
        "CLIENT" -> Color(0xFFF97316)
        "INTERNAL" -> Color(0xFF3B82F6)
        "EXECUTIVE" -> Color(0xFF7C3AED)
        else -> PurplePrimary
    }

    val typeBg = when (meetingTypeUpper) {
        "CLIENT" -> Color(0xFFFFF7ED)
        "INTERNAL" -> Color(0xFFEFF6FF)
        "EXECUTIVE" -> Color(0xFFF3EEFF)
        else -> PurpleSoft
    }

    val typeLabel = when (meetingTypeUpper) {
        "CLIENT" -> "Client"
        "INTERNAL" -> "Internal"
        "EXECUTIVE" -> "Executive"
        else -> event.meetingType.ifBlank { "Meeting" }
    }

    val statusUpper = event.meetingStatus?.uppercase()

    val statusText = when (statusUpper) {
        "PENDING" -> "Pending approval"
        "CANCELLED", "CANCELED" -> "Cancelled"
        else -> "Reserved"
    }

    val statusColor = when (statusUpper) {
        "APPROVED", "ACTIVE"-> Color(0xFF12B76A)
        "PENDING" -> Color(0xFFF59E0B)
        "CANCELLED", "CANCELED" -> Color(0xFFEF4444)
        else -> Color(0xFF12B76A)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 6.dp)
                        .width(44.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0xFFE0E4EC))
                )
            }
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            SheetHeader(
                title = "Meeting details",
                onClose = onDismiss
            )

            Spacer(modifier = Modifier.height(12.dp))

            HeroCard(
                title = event.title,
                typeLabel = typeLabel,
                typeColor = typeColor,
                typeBg = typeBg,
                statusText = statusText,
                statusColor = statusColor,
                dateText = event.date.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                timeText = "${event.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))} – ${event.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(title = "Information")
            Spacer(modifier = Modifier.height(10.dp))

            DetailSectionCard {
                DetailItem(
                    icon = Icons.Outlined.Schedule,
                    title = "Schedule",
                    subtitle = event.date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) +
                        " • " +
                        "${event.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))} – ${event.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
                )

                DividerSpacer()

                DetailItem(
                    icon = Icons.Outlined.Place,
                    title = "Room",
                    subtitle = if (event.meetingRoom.isNotBlank()) event.meetingRoom else "Unassigned"
                )

                DividerSpacer()

                DetailItem(
                    icon = Icons.Outlined.AccountCircle,
                    title = "Organizer",
                    subtitle = if (event.createdBy.isNotBlank()) event.createdBy else "Unknown"
                )

                if (event.description.isNotBlank()) {
                    DividerSpacer()
                    DetailItem(
                        icon = Icons.Outlined.Info,
                        title = "Description",
                        subtitle = event.description
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(title = "Attendees")
            Spacer(modifier = Modifier.height(10.dp))

            DetailSectionCard {
                ExpandableAttendeesItem(event = event)
            }

            Spacer(modifier = Modifier.height(18.dp))

            ActionBar(
                onDismiss = onDismiss,
                onEdit = onEdit
            )
        }
    }
}

@Composable
private fun SheetHeader(
    title: String,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Review meeting information and attendees",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(SurfaceTint)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HeroCard(
    title: String,
    typeLabel: String,
    typeColor: Color,
    typeBg: Color,
    statusText: String,
    statusColor: Color,
    dateText: String,
    timeText: String
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceTint),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(typeColor)
                )

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PillChip(
                    text = typeLabel,
                    backgroundColor = typeBg,
                    contentColor = typeColor
                )

                PillChip(
                    text = "Confirmed",
                    backgroundColor = Color(0xFFEFFAF4),
                    contentColor = Color(0xFF12B76A)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderSoft)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniInfoTile(
                    label = "Date",
                    value = dateText,
                    modifier = Modifier.weight(1f)
                )
                MiniInfoTile(
                    label = "Time",
                    value = timeText,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PillChip(
    text: String,
    backgroundColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = backgroundColor,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, backgroundColor.copy(alpha = 0.7f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MiniInfoTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun DetailSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun DividerSpacer() {
    Spacer(modifier = Modifier.height(14.dp))
    HorizontalDivider(color = BorderSoft)
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceTint),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PurplePrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight
            )
        }
    }
}

@Composable
private fun ExpandableAttendeesItem(event: MeetingEvent) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val internal = event.internalParticipants
    val external = event.externalParticipants
    val total = internal.size + external.size

    val arrowRotation by animateDpAsState(
        targetValue = if (expanded) 180.dp else 0.dp,
        label = "arrowRotation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (total > 0) expanded = !expanded
                }
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = PurplePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Attendees",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$total ${if (total == 1) "person" else "people"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            if (total > 0) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded && total > 0,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, top = 12.dp)
            ) {
                if (internal.isNotEmpty()) {
                    Text(
                        text = "Internal",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    internal.forEach { p ->
                        AttendeeRow(
                            name = listOfNotNull(p.firstName, p.lastName)
                                .joinToString(" ")
                                .trim()
                                .ifBlank { p.email ?: "Unknown" },
                            subtitle = p.email ?: "Internal attendee"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (external.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "External",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    external.forEach { p ->
                        AttendeeRow(
                            name = p.name.ifBlank { "Unknown" },
                            subtitle = p.email ?: "External attendee"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendeeRow(
    name: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PurpleSoft),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.labelMedium,
                color = PurplePrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActionBar(
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BorderSoft),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text("Close")
        }

        Button(
            onClick = onEdit,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Edit booking",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}