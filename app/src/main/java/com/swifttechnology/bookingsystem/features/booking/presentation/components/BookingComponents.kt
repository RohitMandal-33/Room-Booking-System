package com.swifttechnology.bookingsystem.features.booking.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Neutral300
import com.swifttechnology.bookingsystem.core.designsystem.Neutral400
import com.swifttechnology.bookingsystem.core.designsystem.Neutral700
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.TextPrimary
import com.swifttechnology.bookingsystem.core.designsystem.customColors

// Reusable label for form fields. Uses Book Room label color when in theme.
@Composable
fun FieldLabel(text: String, isRequired: Boolean) {
    val labelColor = MaterialTheme.customColors.bookRoomLabel
    Row {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor
        )
        if (isRequired) {
            Text(
                text = " *",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BookingTextField(
    label: String,
    value: String,
    placeholder: String,
    isRequired: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        var isFocused by remember { mutableStateOf(false) }
        FieldLabel(text = label, isRequired = isRequired)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.customColors.bookRoomPlaceholder,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .onFocusChanged { isFocused = it.isFocused }
                .shadow(
                    elevation = if (isFocused) 8.dp else 0.dp,
                    shape = RoundedCornerShape(CornerRadius.lg),
                    clip = false
                ),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                unfocusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                disabledContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                unfocusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )
    }
}

@Composable
fun BookingClickableField(
    label: String,
    value: String,
    placeholder: String?,
    isRequired: Boolean,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    contentBelowLabel: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        FieldLabel(text = label, isRequired = isRequired)
        contentBelowLabel?.invoke()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(MaterialTheme.customColors.bookRoomInputBackground)
                .clickable { onClick() }
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    leadingIcon?.invoke()
                    if (value.isNotEmpty()) {
                        Text(
                            text = value,
                            fontSize = 14.sp,
                            color = MaterialTheme.customColors.bookRoomLabel
                        )
                    } else if (placeholder != null) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = MaterialTheme.customColors.bookRoomPlaceholder
                        )
                    }
                }
                trailingIcon?.invoke()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDropdownField(
    label: String,
    value: String,
    placeholder: String,
    isRequired: Boolean,
    expanded: Boolean,
    options: List<String>,
    leadingIcon: (@Composable () -> Unit)? = null,
    onExpandChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        FieldLabel(text = label, isRequired = isRequired)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandChange
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(MaterialTheme.customColors.bookRoomInputBackground)
                    .menuAnchor()
                    .padding(horizontal = Spacing.md),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        leadingIcon?.invoke()
                        Text(
                            text = value.ifEmpty { placeholder },
                            fontSize = 14.sp,
                            color = if (value.isEmpty()) MaterialTheme.customColors.bookRoomPlaceholder else MaterialTheme.customColors.bookRoomLabel,
                            maxLines = 1
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = Neutral700,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandChange(false) },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        },
                        onClick = { onOptionSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingNavBar(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Sidebar toggle button (left)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, Neutral300, CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { /* toggle sidebar */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_sort_by_size),
                contentDescription = "Sidebar",
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        // Search bar (center)
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.full))
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Search people...",
                    fontSize = 12.sp,
                    color = Neutral400,
                    letterSpacing = 0.4.sp
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Neutral700,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Close button
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = TextPrimary
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val currentTime = java.time.LocalTime.now()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Neutral700)
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectorColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(CornerRadius.lg)
    )
}
