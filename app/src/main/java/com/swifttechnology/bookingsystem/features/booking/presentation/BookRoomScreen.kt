package com.swifttechnology.bookingsystem.features.booking.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.Neutral400
import com.swifttechnology.bookingsystem.core.designsystem.Neutral700
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingClickableField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingDropdownField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingTextField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.ParticipantsSheetContent
import com.swifttechnology.bookingsystem.features.booking.presentation.components.TimePickerDialog
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import com.swifttechnology.bookingsystem.shared.layout.BottomSheetView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookRoomScreen(
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    onSubmit: (RoomBookingFormState) -> Unit = {},
    viewModel: BookRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var isContentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isContentVisible = true }

    val formState = uiState.formState

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showMeetingTypeDropdown by remember { mutableStateOf(false) }
    var showParticipantsSheet by remember { mutableStateOf(false) }
    var showRoomDropdown by remember { mutableStateOf(false) }

    val participantsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val meetingTypes = listOf("Team Meeting", "Client Call", "Workshop", "Interview", "Training")
    val availableRooms = listOf("Room A - Horizon", "Room B - Summit", "Room C - Nexus", "Room D - Eclipse")
    val availableParticipants = listOf("Alice Johnson", "Bob Smith", "Carol White", "David Lee")

    MainScaffold(
        sidebarItems = uiState.sidebarItems,
        selectedItem = uiState.selectedItem,
        searchQuery = uiState.searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSidebarItemSelected = { item ->
            viewModel.onSidebarItemSelected(item)
            onNavigate(item.route)
        },
        onLogout = {
            scope.launch {
                viewModel.logout()
                onLogout()
            }
        },
        containerColor = MaterialTheme.customColors.whitePure,
        searchPlaceholder = "Search people, participants..."
    ) {
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                BookingTextField(
                    label = "Meeting Title",
                    value = formState.meetingTitle,
                    placeholder = "Enter meeting title",
                    isRequired = true,
                    onValueChange = { viewModel.onFormStateChanged(formState.copy(meetingTitle = it)) }
                )
                BookingDropdownField(
                    label = "Meeting Type",
                    value = formState.meetingType,
                    placeholder = "Select Meeting Type",
                    isRequired = true,
                    expanded = showMeetingTypeDropdown,
                    options = meetingTypes,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_manage),
                            contentDescription = null,
                            tint = Neutral700,
                            modifier = Modifier.size(20.dp)
                        )
                    },

                    onExpandChange = { showMeetingTypeDropdown = it },
                    onOptionSelected = {
                        viewModel.onFormStateChanged(formState.copy(meetingType = it))
                        showMeetingTypeDropdown = false
                    }
                )
                BookingDropdownField(
                    label = "Select Room",
                    value = formState.selectedRoom,
                    placeholder = "Choose a meeting room",
                    isRequired = true,
                    expanded = showRoomDropdown,
                    options = availableRooms,
                    onExpandChange = { showRoomDropdown = it },
                    onOptionSelected = {
                        viewModel.onFormStateChanged(formState.copy(selectedRoom = it))
                        showRoomDropdown = false
                    }
                )

                BookingClickableField(
                    label = "",
                    value = if (formState.selectedRoom.isNotEmpty()) "Select a room to view details" else "",
                    placeholder = "Select a room to view details",
                    isRequired = false,
                    onClick = { showRoomDropdown = true }
                )

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
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    onClick = { showDatePicker = true }
                )

                BookingClickableField(
                    label = "Start Time",
                    value = formState.startTime,
                    placeholder = "Select start time",
                    isRequired = true,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_recent_history),
                            contentDescription = "Time",
                            tint = Neutral400,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = { showStartTimePicker = true }
                )

                BookingClickableField(
                    label = "End Time",
                    value = formState.endTime,
                    placeholder = "Select end time",
                    isRequired = true,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_recent_history),
                            contentDescription = "Time",
                            tint = Neutral400,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = { showEndTimePicker = true }
                )

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
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = { showParticipantsSheet = true }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = "Book Room",
                    onClick = { onSubmit(formState) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

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

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { hour, minute ->
                viewModel.onFormStateChanged(formState.copy(startTime = "%02d:%02d".format(hour, minute)))
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = { hour, minute ->
                viewModel.onFormStateChanged(formState.copy(endTime = "%02d:%02d".format(hour, minute)))
                showEndTimePicker = false
            }
        )
    }

    if (showParticipantsSheet) {
        BottomSheetView(
            onDismiss = { showParticipantsSheet = false },
            sheetState = participantsSheetState,
            title = null
        ) {
            ParticipantsSheetContent(
                formState = formState,
                onFormStateChanged = viewModel::onFormStateChanged,
                onClose = { showParticipantsSheet = false }

            )
        }
    }
}

private fun buildParticipantsSummary(formState: RoomBookingFormState): String {
    val internalNames = formState.participants.map { it.substringBefore("|").ifEmpty { it } }
    val external = formState.externalMembers.map { "${it.name} (${it.email})" }
    return (internalNames + external).joinToString(", ").takeIf { it.isNotEmpty() } ?: ""
}
