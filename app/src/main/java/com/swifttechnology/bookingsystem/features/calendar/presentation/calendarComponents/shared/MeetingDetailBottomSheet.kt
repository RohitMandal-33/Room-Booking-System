package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.swifttechnology.bookingsystem.core.designsystem.Error
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.Success
import com.swifttechnology.bookingsystem.core.designsystem.Warning
import com.swifttechnology.bookingsystem.core.designsystem.adaptiveHorizontalScreenPadding
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.core.utils.ColorUtils
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

  
// Root bottom sheet
  

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingDetailBottomSheet(
    event: MeetingEvent,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = isSystemInDarkTheme()
    val horizontalPad = adaptiveHorizontalScreenPadding()

    val typeColor = event.backendColor ?: event.color
    val typeBg = ColorUtils.getLightBackgroundColor(typeColor, isDark)
    val typeLabel = event.meetingType.lowercase()
        .replaceFirstChar { it.uppercase() }
        .ifBlank { "Meeting" }

    val now = LocalDateTime.now()
    val meetingStart = LocalDateTime.of(event.date, event.startTime)
    val meetingEnd = LocalDateTime.of(event.date, event.endTime)
    val statusText = when {
        event.date == LocalDate.now() -> "Today"
        meetingEnd.isBefore(now)      -> "Passed"
        meetingStart.isAfter(now)     -> "Upcoming"
        else                          -> "Today"
    }
    val statusColor = when (statusText) {
        "Passed"   -> Error
        "Today"    -> Warning
        else       -> Success
    }

    val sheetMaxWidth = minOf(560.dp, LocalConfiguration.current.screenWidthDp.dp - 24.dp)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = sheetMaxWidth),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        contentWindowInsets = { WindowInsets.systemBars },
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad)
                    .padding(top = Spacing.xl, bottom = Spacing.md)
            ) {
                SheetHeader(title = "Meeting details", onClose = onDismiss)
            }

            // Scrollable body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPad)
            ) {
                HeroCard(
                    title = event.title,
                    typeLabel = typeLabel,
                    typeColor = typeColor,
                    typeBg = typeBg,
                    statusText = statusText,
                    statusColor = statusColor,
                    dateText = event.date.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                    timeText = "${event.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))} – ${
                        event.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                    }"
                )

                Spacer(modifier = Modifier.height(Spacing.md))
                SectionTitle(title = "Information")
                Spacer(modifier = Modifier.height(Spacing.sm + Spacing.xxs))

                DetailSectionCard {
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
                    if (!event.recurrenceType.isNullOrBlank() && event.recurrenceType != "NONE") {
                        DividerSpacer()
                        DetailItem(
                            icon = Icons.Outlined.Schedule,
                            title = "Recurrence",
                            subtitle = buildString {
                                append(formatRecurrenceType(event.recurrenceType))
                                val start = event.recurrenceStartDate
                                val end   = event.recurrenceEndDate
                                if (start != null && end != null) {
                                    append(" • ")
                                    append(start.format(DateTimeFormatter.ofPattern("MMM d, yyyy")))
                                    append(" - ")
                                    append(end.format(DateTimeFormatter.ofPattern("MMM d, yyyy")))
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.md))
                SectionTitle(title = "Attendees")
                Spacer(modifier = Modifier.height(Spacing.sm + Spacing.xxs))

                DetailSectionCard {
                    ExpandableAttendeesItem(event = event)
                }

                Spacer(modifier = Modifier.height(Spacing.lg))
            }

            // Action bar with popup dialogs
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad)
                    .padding(top = Spacing.md, bottom = Spacing.lg)
            ) {
                ActionBar(event = event, onEdit = onEdit, onDelete = onDelete)
            }
        }
    }
}

  
// ActionBar hosts both popups and the two buttons
  

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ActionBar(
    event: MeetingEvent,
    onEdit: (String) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    val now = LocalDateTime.now()
    val meetingStart = LocalDateTime.of(event.date, event.startTime)
    val meetingEnd = LocalDateTime.of(event.date, event.endTime)

    val isPast = now.isAfter(meetingEnd)
    val isOngoing = !now.isBefore(meetingStart) && !now.isAfter(meetingEnd)

    if (isPast) return

    var showDeletePopup by remember { mutableStateOf(false) }
    var showEditPopup   by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        val popupWidth = maxWidth
        val hasTwoButtons = onDelete != null && !isOngoing

        // Delete popup floats above the Delete button (right side)
        if (showDeletePopup && hasTwoButtons) {
            DeletePopup(
                event     = event,
                popupWidth = popupWidth,
                hasTwoButtons = hasTwoButtons,
                onDismiss = { showDeletePopup = false },
                onConfirm = {
                    showDeletePopup = false
                    val id = event.meetingId ?: event.id.toLong()
                    onDelete?.invoke(id)
                }
            )
        }

        // Edit popup floats above the Edit button (left side)
        if (showEditPopup) {
            EditPopup(
                event     = event,
                popupWidth = popupWidth,
                hasTwoButtons = hasTwoButtons,
                onDismiss = { showEditPopup = false },
                onEdit    = onEdit
            )
        }

        // Buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    showDeletePopup = false
                    showEditPopup   = !showEditPopup
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (hasTwoButtons && onDelete != null) {
                OutlinedButton(
                    onClick = {
                        showEditPopup   = false
                        showDeletePopup = !showDeletePopup
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, Error),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Cancel meeting",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Delete", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

  
// Delete Popup
  

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DeletePopup(
    event: MeetingEvent,
    popupWidth: Dp,
    hasTwoButtons: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val yOffset = with(LocalDensity.current) { -68.dp.roundToPx() }
    Popup(
        alignment = Alignment.BottomCenter,
        offset = IntOffset(x = 0, y = yOffset),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.customColors.divider),
                shadowElevation = 8.dp,
                modifier = Modifier.width(popupWidth)
            ) {
                Column {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(Error.copy(alpha = 0.08f))
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Error.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cancel Meeting",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "This action cannot be undone",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.customColors.textSecondary
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.customColors.divider)

                    Column(modifier = Modifier.padding(20.dp)) {
                        // Meeting preview chip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.customColors.surfaceLight)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Error)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = event.date.atTime(event.startTime).format(
                                        DateTimeFormatter.ofPattern("EEE, MMM d · hh:mm a")
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.customColors.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.customColors.divider)
                            ) {
                                Text(
                                    text = "Keep",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Button(
                                onClick = onConfirm,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Error)
                            ) {
                                Text(
                                    text = "Cancel Meeting",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            // Caret tail points toward the Delete button (right side)
            PopupTail(tailAlignment = Alignment.End, hasTwoButtons = hasTwoButtons)
        }
    }
}

  
// Edit Popup
  

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditPopup(
    event: MeetingEvent,
    popupWidth: Dp,
    hasTwoButtons: Boolean,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit
) {
    val isRecurring = !event.recurrenceId.isNullOrBlank() && event.recurrenceType != "NONE"
    val primary = MaterialTheme.colorScheme.primary
    val yOffset = with(LocalDensity.current) { -68.dp.roundToPx() }

    Popup(
        alignment = Alignment.BottomCenter,
        offset = IntOffset(x = 0, y = yOffset),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.customColors.divider),
                shadowElevation = 8.dp,
                modifier = Modifier.width(popupWidth)
            ) {
                Column {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(MaterialTheme.customColors.primaryLight)
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.customColors.primaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Edit Meeting",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isRecurring) "Choose how to apply changes"
                                else "Confirm to proceed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.customColors.textSecondary
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.customColors.divider)

                    Column(modifier = Modifier.padding(20.dp)) {
                        if (isRecurring) {
                            // Scope option this event only
                            EditScopeCard(
                                icon = Icons.Outlined.Schedule,
                                title = "This event only",
                                subtitle = event.date.format(
                                    DateTimeFormatter.ofPattern("EEEE, MMM d")
                                ),
                                onClick = { onEdit("THIS"); onDismiss() }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Scope option all events
                            EditScopeCard(
                                icon = Icons.Outlined.Group,
                                title = "All events in series",
                                subtitle = "Apply to every occurrence",
                                onClick = { onEdit("ALL"); onDismiss() }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.customColors.divider)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            // Meeting preview chip
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.customColors.surfaceLight)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(primary)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = event.date.atTime(event.startTime).format(
                                            DateTimeFormatter.ofPattern("EEE, MMM d · hh:mm a")
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.customColors.textSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.customColors.divider)
                                ) {
                                    Text(
                                        text = "Cancel",
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Button(
                                    onClick = { onEdit("ASK"); onDismiss() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                                ) {
                                    Text(
                                        text = "Edit Meeting",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // Caret tail points toward the Edit button (left side)
            PopupTail(tailAlignment = Alignment.Start, hasTwoButtons = hasTwoButtons)
        }
    }
}

  
// Downward-pointing caret the "thought cloud" tail
  

@Composable
private fun PopupTail(tailAlignment: Alignment.Horizontal, hasTwoButtons: Boolean) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val borderColor  = MaterialTheme.customColors.divider

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = if (hasTwoButtons) Modifier.weight(1f) else Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (tailAlignment == Alignment.Start || !hasTwoButtons) {
                TailTriangle(surfaceColor, borderColor)
            }
        }

        if (hasTwoButtons) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (tailAlignment == Alignment.End) {
                    TailTriangle(surfaceColor, borderColor)
                }
            }
        }
    }
}

@Composable
private fun TailTriangle(surfaceColor: Color, borderColor: Color) {
    Canvas(modifier = Modifier.size(width = 24.dp, height = 14.dp)) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width / 2f, size.height)
            lineTo(size.width, 0f)
            close()
        }
        drawPath(path, color = surfaceColor)
        drawPath(
            path  = path,
            color = borderColor,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

  
// Edit Scope Option Card  (recurring events only)
  

@Composable
private fun EditScopeCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.customColors.divider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.customColors.primaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.customColors.textSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.customColors.textSecondary,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { rotationZ = -90f }
            )
        }
    }
}

  
// Shared sub-components
  

private fun formatRecurrenceType(type: String?): String {
    return when (type?.uppercase()) {
        "DAILY"   -> "Repeats daily"
        "WEEKDAY" -> "Repeats on weekdays"
        "WEEKLY"  -> "Repeats weekly"
        "MONTHLY" -> "Repeats monthly"
        "YEARLY"  -> "Repeats yearly"
        "CUSTOM"  -> "Custom recurrence"
        else      -> "Does not repeat"
    }
}

@Composable
private fun SheetHeader(title: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                color = MaterialTheme.customColors.textSecondary
            )
        }
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(MaterialTheme.customColors.surfaceLight)
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
    androidx.compose.material3.Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.customColors.surfaceLight
        ),
        border = BorderStroke(1.dp, MaterialTheme.customColors.divider),
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
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = typeBg,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, typeBg.copy(alpha = 0.7f))
            ) {
                Text(
                    text = typeLabel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = typeColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.customColors.divider)
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniInfoTile(label = "Date", value = dateText, modifier = Modifier.weight(1f))
                MiniInfoTile(label = "Time", value = timeText, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MiniInfoTile(label: String, value: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.customColors.divider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.customColors.textSecondary,
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
    androidx.compose.material3.Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.customColors.divider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun DividerSpacer() {
    Spacer(modifier = Modifier.height(14.dp))
    HorizontalDivider(color = MaterialTheme.customColors.divider)
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun DetailItem(icon: ImageVector, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.customColors.primaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
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
                color = MaterialTheme.customColors.textSecondary,
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
    val total    = internal.size + external.size

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { if (total > 0) expanded = !expanded }
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.customColors.primaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
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
                        color = MaterialTheme.customColors.textSecondary
                    )
                }
            }
            if (total > 0) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.customColors.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = expanded && total > 0,
            enter = fadeIn() + expandVertically(),
            exit  = fadeOut() + shrinkVertically()
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
                        color = MaterialTheme.customColors.textSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    internal.forEach { p ->
                        AttendeeRow(
                            name = listOfNotNull(p.firstName, p.lastName)
                                .joinToString(" ").trim()
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
                        color = MaterialTheme.customColors.textSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    external.forEach { p ->
                        AttendeeRow(
                            name     = p.name.ifBlank { "Unknown" },
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
private fun AttendeeRow(name: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(MaterialTheme.customColors.primaryLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
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
                color = MaterialTheme.customColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}