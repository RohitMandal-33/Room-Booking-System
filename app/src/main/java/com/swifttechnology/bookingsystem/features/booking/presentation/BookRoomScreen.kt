package com.swifttechnology.bookingsystem.features.booking.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.R
import com.swifttechnology.bookingsystem.core.designsystem.Neutral400
import com.swifttechnology.bookingsystem.core.designsystem.Neutral700
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingClickableField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingDropdownField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingTextField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.ParticipantsSheetContent
import com.swifttechnology.bookingsystem.features.booking.presentation.components.SelectedParticipantChip
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton
import com.swifttechnology.bookingsystem.features.main.presentation.PendingBookingDetails
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import com.swifttechnology.bookingsystem.shared.layout.BottomSheetView
import kotlinx.coroutines.launch
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.shared.components.rooms.RoomCard
import com.swifttechnology.bookingsystem.shared.components.rooms.RoomInfoCard
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRoomScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    onSubmit: (RoomBookingFormState) -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    initialRoomName: String? = null,
    initialDetails: PendingBookingDetails? = null,
    onNavigateToCalendar: (PendingBookingDetails) -> Unit = {},
    viewModel: BookRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success Snackbar
    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) {
            snackbarHostState.showSnackbar(
                message = "Room booked successfully!",
                duration = SnackbarDuration.Short
            )
            viewModel.clearSubmitSuccess()
        }
    }

    // Show error Snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.availableRooms, initialRoomName, initialDetails) {
        if (uiState.availableRooms.isNotEmpty()) {
            if (initialDetails != null) {
                viewModel.prefillBookingDetails(
                    roomName = initialDetails.roomName,
                    date = initialDetails.date,
                    startTime = initialDetails.startTime,
                    endTime = initialDetails.endTime
                )
            } else if (initialRoomName != null) {
                viewModel.preSelectRoom(initialRoomName)
            }
        }
    }

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    var isContentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isContentVisible = true }

    val formState = uiState.formState

    var showDatePicker by remember { mutableStateOf(false) }
    var showMeetingTypeDropdown by remember { mutableStateOf(false) }
    var showRecurringDropdown by remember { mutableStateOf(false) }
    var showParticipantsSheet by remember { mutableStateOf(false) }
    var showRoomDropdown by remember { mutableStateOf(false) }

    val participantsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val meetingTypes = listOf("Internal", "Client", "Executive")
    val meetingTypeIcons: Map<String, @Composable () -> Unit> = mapOf(
        "Internal" to {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3)) // Blue
            )
        },
        "Client" to {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF9800)) // Orange
            )
        },
        "Executive" to {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF9C27B0)) // Purple
            )
        }
    )

    val recurringOptions = listOf(
        "Does not repeat",
        "Every weekday (Sun-Fri)",
        "Daily",
        "Weekly",
        "Monthly",
        "Custom"
    )

    val availableRooms = uiState.availableRooms.map { it.name }
    val selectedInternalKeys = formState.participants
    val selectedExternalMembers = formState.externalMembers
    val hasAnySelected = selectedInternalKeys.isNotEmpty() || selectedExternalMembers.isNotEmpty()

    Box(modifier = Modifier.fillMaxWidth()) {
    AnimatedVisibility(
        visible = isContentVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)) +
            slideInVertically(
                initialOffsetY = { fullHeight -> (fullHeight * 0.15f).toInt() },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ),
        exit = fadeOut(animationSpec = tween(durationMillis = 200)) +
            slideOutVertically(targetOffsetY = { fullHeight -> (fullHeight * 0.1f).toInt() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(Spacing.ml)
        ) {
            // 1. Meeting Title
            BookingTextField(
                label = "Meeting Title",
                value = formState.meetingTitle,
                placeholder = "Enter meeting title",
                isRequired = true,
                onValueChange = { viewModel.onFormStateChanged(formState.copy(meetingTitle = it)) }
            )

            // 2. Select Room
            BookingDropdownField(
                label = "Select Room",
                value = formState.selectedRoom,
                placeholder = "Choose a meeting room",
                isRequired = true,
                expanded = showRoomDropdown,
                options = availableRooms,
                onExpandChange = { showRoomDropdown = it },
                onOptionSelected = { selected ->
                    val selectedRoom = uiState.availableRooms.find { it.name == selected }
                    viewModel.onFormStateChanged(formState.copy(
                        selectedRoom = selected,
                        selectedRoomId = selectedRoom?.id
                    ))
                    showRoomDropdown = false
                }
            )

            // Room info card when a room is selected
            uiState.availableRooms.find { it.name == formState.selectedRoom }?.let {
                RoomInfoCard(
                    room = it,
                )
            }

            // Date
            BookingClickableField(
                label = "Date",
                value = formState.date,
                placeholder = "Select date",
                isRequired = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = Neutral700,
                        modifier = Modifier.size(Spacing.lg)
                    )
                },
                onClick = { showDatePicker = true }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.ms)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BookingClickableField(
                        label = "Start Time",
                        value = formState.startTime,
                        placeholder = "Select time",
                        isRequired = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_recent_history),
                                contentDescription = "Time",
                                tint = Neutral400,
                                modifier = Modifier.size(Spacing.ml)
                            )
                        },
                        onClick = { 
                            onNavigateToCalendar(
                                PendingBookingDetails(
                                    roomName = formState.selectedRoom,
                                    date = formState.date,
                                    startTime = formState.startTime,
                                    endTime = formState.endTime
                                )
                            )
                        }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    BookingClickableField(
                        label = "End Time",
                        value = formState.endTime,
                        placeholder = "Select time",
                        isRequired = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_recent_history),
                                contentDescription = "Time",
                                tint = Neutral400,
                                modifier = Modifier.size(Spacing.ml)
                            )
                        },
                        onClick = { 
                            onNavigateToCalendar(
                                PendingBookingDetails(
                                    roomName = formState.selectedRoom,
                                    date = formState.date,
                                    startTime = formState.startTime,
                                    endTime = formState.endTime
                                )
                            )
                        }
                    )
                }
            }

            BookingDropdownField(
                label = "Meeting Type",
                value = formState.meetingType,
                placeholder = "Select meeting type",
                isRequired = true,
                expanded = showMeetingTypeDropdown,
                options = meetingTypes,
                optionLeadingIcons = meetingTypeIcons,
                onExpandChange = { showMeetingTypeDropdown = it },
                onOptionSelected = {
                    viewModel.onFormStateChanged(formState.copy(meetingType = it))
                    showMeetingTypeDropdown = false
                }
            )

            //  Recurring
            BookingDropdownField(
                label = "Recurring",
                value = formState.recurringType,
                placeholder = "Does not repeat",
                isRequired = false,
                expanded = showRecurringDropdown,
                options = recurringOptions,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_repeat),
                        contentDescription = "Recurring",
                        tint = Neutral700,
                        modifier = Modifier.size(Spacing.ml)
                    )
                },
                onExpandChange = { showRecurringDropdown = it },
                onOptionSelected = {
                    viewModel.onFormStateChanged(formState.copy(recurringType = it))
                    showRecurringDropdown = false
                }
            )

            // Description
            BookingTextField(
                label = "Description",
                value = formState.description,
                placeholder = "Add meeting description (optional)",
                isRequired = false,
                onValueChange = { viewModel.onFormStateChanged(formState.copy(description = it)) }
            )

            // Participants
            BookingClickableField(
                label = "Participants",
                value = buildParticipantsSummary(formState),
                placeholder = "Add Participants",
                isRequired = false,
                leadingIcon = {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_myplaces),
                        contentDescription = null,
                        tint = Neutral700,
                        modifier = Modifier.size(Spacing.ml)
                    )
                },
                contentBelowLabel = {
                    if (hasAnySelected) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            selectedInternalKeys.forEach { member ->
                                val name = member.name
                                SelectedParticipantChip(
                                    name = name,
                                    onRemove = {
                                        viewModel.onFormStateChanged(
                                            formState.copy(participants = formState.participants - member)
                                        )
                                    }
                                )
                            }
                            selectedExternalMembers.forEach { member ->
                                SelectedParticipantChip(
                                    name = member.name,
                                    onRemove = {
                                        viewModel.onFormStateChanged(
                                            formState.copy(externalMembers = formState.externalMembers - member)
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                onClick = { showParticipantsSheet = true }
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Book button
            PrimaryButton(
                text = if (uiState.isSubmitting) "Booking..." else "Book Meeting Room",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                enabled = !uiState.isSubmitting,
                onClick = { viewModel.submitBooking() }
            )

            Spacer(modifier = Modifier.height(Spacing.md))
        }
    }

    // Snackbar host overlaid at the bottom
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.align(Alignment.BottomCenter)
    )
    } // end Box

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = java.text.SimpleDateFormat(
                            "dd MMM yyyy", java.util.Locale.getDefault()
                        ).format(java.util.Date(millis))
                        viewModel.onFormStateChanged(formState.copy(date = formatted))
                    }
                    showDatePicker = false
                }) { Text("OK", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Neutral700)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showParticipantsSheet) {
        BottomSheetView(
            onDismiss = { showParticipantsSheet = false },
            sheetState = participantsSheetState,
            title = null
        ) {
            ParticipantsSheetContent(
                formState = formState,
                availableParticipants = uiState.availableParticipants,
                onFormStateChanged = viewModel::onFormStateChanged,
                onClose = { showParticipantsSheet = false },
                searchQuery = uiState.participantSearchQuery,
                onSearchQueryChange = viewModel::onParticipantSearchQueryChanged,
                isSearching = uiState.isSearchingParticipants
            )
        }
    }
}

private fun buildParticipantsSummary(formState: RoomBookingFormState): String {
    val internalNames = formState.participants.map { it.name.ifEmpty { "Unknown" } }
    val external = formState.externalMembers.map { "${it.name} (${it.email})" }
    return (internalNames + external).joinToString(", ").takeIf { it.isNotEmpty() } ?: ""
}

