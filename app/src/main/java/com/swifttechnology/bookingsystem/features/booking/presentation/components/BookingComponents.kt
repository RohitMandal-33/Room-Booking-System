package com.swifttechnology.bookingsystem.features.booking.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions

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
    onValueChange: (String) -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var isFocused by remember { mutableStateOf(false) }
    val primaryColor = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
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
                    elevation = if (isFocused) 12.dp else 0.dp,
                    shape = RoundedCornerShape(CornerRadius.lg),
                    clip = false,
                    ambientColor = if (isFocused) primaryColor.copy(alpha = 0.25f) else Color.Transparent,
                    spotColor = if (isFocused) primaryColor.copy(alpha = 0.55f) else Color.Transparent
                ),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                unfocusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                disabledContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                unfocusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions
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
    var isPressed by remember { mutableStateOf(false) }
    val primaryColor = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        FieldLabel(text = label, isRequired = isRequired)
        contentBelowLabel?.invoke()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = if (isPressed) 12.dp else 0.dp,
                    shape = RoundedCornerShape(CornerRadius.lg),
                    clip = false,
                    ambientColor = if (isPressed) primaryColor.copy(alpha = 0.25f) else Color.Transparent,
                    spotColor = if (isPressed) primaryColor.copy(alpha = 0.55f) else Color.Transparent
                )
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(MaterialTheme.customColors.bookRoomInputBackground)
                .then(
                    if (isPressed) Modifier.border(
                        1.5.dp,
                        primaryColor.copy(alpha = 0.6f),
                        RoundedCornerShape(CornerRadius.lg)
                    ) else Modifier
                )
                .clickable {
                    isPressed = !isPressed
                    onClick()
                }
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
    optionLeadingIcons: Map<String, @Composable () -> Unit>? = null,
    onExpandChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    // Capture in composable scope for use in shadow modifier
    val primaryColor = MaterialTheme.colorScheme.primary

    // Animate arrow rotation: 0° when collapsed, 180° when expanded
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "arrowRotation"
    )

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
                    .shadow(
                        elevation = if (expanded) 12.dp else 0.dp,
                        shape = RoundedCornerShape(CornerRadius.lg),
                        clip = false,
                        ambientColor = if (expanded) primaryColor.copy(alpha = 0.25f) else Color.Transparent,
                        spotColor = if (expanded) primaryColor.copy(alpha = 0.55f) else Color.Transparent
                    )
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
                            color = if (value.isEmpty()) MaterialTheme.customColors.bookRoomPlaceholder
                            else MaterialTheme.customColors.bookRoomLabel,
                            maxLines = 1
                        )
                    }
                    // Animated rotating arrow icon
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = Neutral700,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(arrowRotation)
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandChange(false) },
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(CornerRadius.lg),
                        ambientColor = Color.Black.copy(alpha = 0.08f),
                        spotColor = Color.Black.copy(alpha = 0.12f)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(CornerRadius.lg)
                    )
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .padding(vertical = Spacing.xs)
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                optionLeadingIcons?.get(option)?.invoke()
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    color = if (option == value) primaryColor else TextPrimary,
                                    fontWeight = if (option == value) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        },
                        modifier = Modifier.background(
                            if (option == value) primaryColor.copy(alpha = 0.08f)
                            else Color.Transparent
                        ),
                        onClick = {
                            onOptionSelected(option)
                            onExpandChange(false)
                        }
                    )
                    // Divider between each item, skipped after the last one
                    if (index < options.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = Spacing.md),
                            thickness = 0.5.dp,
                            color = Neutral300
                        )
                    }
                }
            }
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