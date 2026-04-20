package com.swifttechnology.bookingsystem.features.announcements.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.Neutral700
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementRequestDTO
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingClickableField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingTextField
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAnnouncementSheet(
    editingAnnouncement: Announcement?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (AnnouncementRequestDTO) -> Unit
) {
    val isEdit = editingAnnouncement != null
    val colors = MaterialTheme.customColors

    // Pre-fill state from editing announcement or blank defaults
    var title by rememberSaveable(editingAnnouncement) {
        mutableStateOf(editingAnnouncement?.title ?: "")
    }
    var message by rememberSaveable(editingAnnouncement) {
        mutableStateOf(editingAnnouncement?.message ?: "")
    }
    var priorityLevel by rememberSaveable(editingAnnouncement) {
        mutableStateOf(editingAnnouncement?.priorityLevel ?: "NORMAL")
    }
    var pinned by rememberSaveable(editingAnnouncement) {
        mutableStateOf(editingAnnouncement?.pinned ?: false)
    }
    var startDate by rememberSaveable(editingAnnouncement) {
        mutableStateOf(formatToDisplayDate(editingAnnouncement?.startDate ?: ""))
    }
    var endDate by rememberSaveable(editingAnnouncement) {
        mutableStateOf(formatToDisplayDate(editingAnnouncement?.endDate ?: ""))
    }
    var allUser by rememberSaveable { mutableStateOf(true) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val canSave = title.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .navigationBarsPadding()
                .imePadding()
                .padding(bottom = Spacing.lg)
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Announcement",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.deepBlack
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.deepBlack
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.ml)
            ) {
                // Meeting Title
                BookingTextField(
                    label = "Meeting Title",
                    value = title,
                    placeholder = "Enter meeting title",
                    isRequired = true,
                    onValueChange = { title = it }
                )

                // Description
                BookingTextField(
                    label = "Description",
                    value = message,
                    placeholder = "Enter Details",
                    isRequired = false,
                    singleLine = false,
                    minLines = 4,
                    onValueChange = { message = it }
                )

                // Dates row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.ms)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BookingClickableField(
                            label = "Start Date",
                            value = startDate,
                            placeholder = "Start Date",
                            isRequired = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Neutral700,
                                    modifier = Modifier.size(Spacing.lg)
                                )
                            },
                            onClick = { showStartDatePicker = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        BookingClickableField(
                            label = "End Date",
                            value = endDate,
                            placeholder = "End Date",
                            isRequired = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Neutral700,
                                    modifier = Modifier.size(Spacing.lg)
                                )
                            },
                            onClick = { showEndDatePicker = true }
                        )
                    }
                }

                // Pinned toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pinned",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.deepBlack
                        )
                    )
                    Switch(
                        checked = pinned,
                        onCheckedChange = { pinned = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = colors.neutral300
                        )
                    )
                }

                Spacer(Modifier.height(Spacing.xl))

                // Footer section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    PrimaryButton(
                        text = if (isEdit) "Update Announcement" else "Post Announcement",
                        onClick = {
                            if (canSave) {
                                onSave(
                                    AnnouncementRequestDTO(
                                        title     = title.trim(),
                                        message   = message.trim(),
                                        pinned    = pinned,
                                        startDate = convertToApiDate(startDate),
                                        endDate   = convertToApiDate(endDate)
                                    )
                                )
                            }
                        },
                        enabled = canSave
                    )
                    
                    Text(
                        text = "Please fill in all required fields (*)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }

    // Start Date Picker
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = java.text.SimpleDateFormat(
                            "dd MMM yyyy", java.util.Locale.getDefault()
                        ).format(java.util.Date(millis))
                        startDate = formatted
                    }
                    showStartDatePicker = false
                }) { Text("OK", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel", color = Neutral700)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // End Date Picker
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = java.text.SimpleDateFormat(
                            "dd MMM yyyy", java.util.Locale.getDefault()
                        ).format(java.util.Date(millis))
                        endDate = formatted
                    }
                    showEndDatePicker = false
                }) { Text("OK", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel", color = Neutral700)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun convertToApiDate(displayDate: String): String? {
    if (displayDate.isBlank()) return null
    return try {
        val inputFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val date = inputFormat.parse(displayDate) ?: return null
        outputFormat.format(date)
    } catch (e: Exception) {
        null
    }
}

/**
 * Converts API format "yyyy-MM-dd" to UI format "dd MMM yyyy"
 */
private fun formatToDisplayDate(apiDate: String): String {
    if (apiDate.isBlank()) return ""
    return try {
        // Try parsing from YYYY-MM-DD
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        val date = inputFormat.parse(apiDate) ?: return apiDate
        outputFormat.format(date)
    } catch (e: Exception) {
        apiDate
    }
}
