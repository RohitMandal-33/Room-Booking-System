package com.swifttechnology.bookingsystem.features.booking.presentation

import android.os.Build
import android.graphics.Color as AndroidColor
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
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
import com.swifttechnology.bookingsystem.features.booking.presentation.components.ExternalParticipantsBottomSheet
import com.swifttechnology.bookingsystem.features.booking.presentation.components.InternalParticipantsBottomSheet
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton
import com.swifttechnology.bookingsystem.features.main.presentation.PendingBookingDetails
import com.swifttechnology.bookingsystem.shared.layout.BottomSheetView
import com.swifttechnology.bookingsystem.shared.components.rooms.RoomInfoCard
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.swifttechnology.bookingsystem.features.booking.presentation.components.SelectedParticipantChip

/**
 * Parses all colour string formats the backend may return:
 *  - "#RRGGBB"
 *  - "rgb(R,G,B)"
 *  - bare "(R,G,B)" (missing the 'rgb' prefix)
 * Falls back to a default purple on any parse failure.
 */
private fun parseMeetingTypeColor(colorCode: String?): Color {
    val fallback = Color(0xFF6C3EE8)
    if (colorCode.isNullOrBlank()) return fallback
    return try {
        when {
            colorCode.startsWith("#") -> Color(AndroidColor.parseColor(colorCode))
            colorCode.startsWith("rgb") || colorCode.startsWith("(") -> {
                val nums = Regex("""(\d+)\s*,\s*(\d+)\s*,\s*(\d+)""")
                    .find(colorCode)?.destructured
                    ?: return fallback
                val (r, g, b) = nums
                Color(r.toInt() / 255f, g.toInt() / 255f, b.toInt() / 255f)
            }
            else -> fallback
        }
    } catch (e: Exception) { fallback }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookRoomScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    onSubmit: (RoomBookingFormState) -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    initialRoomName: String? = null,
    initialDetails: PendingBookingDetails? = null,
    onNavigateToCalendar: (PendingBookingDetails) -> Unit = {},
    pendingTimeUpdate: Triple<String, String, String>? = null,
    onTimeUpdate: ((String, String, String) -> Unit)? = null,
    onSuccess: () -> Unit = {},
    viewModel: BookRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success callback
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is BookRoomEvent.NavigateBack) {
                kotlinx.coroutines.delay(100)
                onSuccess()
            }
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

    val wasPrefilledRef = remember { mutableStateOf(false) }
    // Include availableParticipants in the key set so this re-fires once the participant
    // list arrives from the API (it loads asynchronously via a Flow).
    LaunchedEffect(uiState.availableRooms, uiState.availableMeetingTypes, uiState.availableParticipants, initialRoomName, initialDetails) {
        if (uiState.availableRooms.isNotEmpty() && !wasPrefilledRef.value) {
            // Wait for meeting types too before pre-filling so meetingTypeId resolves correctly
            if (initialDetails != null && uiState.availableMeetingTypes.isEmpty()) return@LaunchedEffect
            // If this booking has internal participants, wait until the participant list has
            // loaded so that the prefill can actually match them by id.
            if (initialDetails != null
                && initialDetails.internalParticipantIds.isNotEmpty()
                && uiState.availableParticipants.isEmpty()
            ) return@LaunchedEffect
            if (initialDetails != null) {
                wasPrefilledRef.value = true
                viewModel.prefillBookingDetails(
                    bookingId             = initialDetails.bookingId,
                    roomName              = initialDetails.roomName,
                    date                  = initialDetails.date,
                    startTime             = initialDetails.startTime,
                    endTime               = initialDetails.endTime,
                    meetingTitle          = initialDetails.meetingTitle,
                    meetingType           = initialDetails.meetingType,
                    meetingTypeId          = initialDetails.meetingTypeId,
                    description           = initialDetails.description,
                    internalParticipantIds = initialDetails.internalParticipantIds,
                    externalMembers       = initialDetails.externalMembers,
                    recurrenceId          = initialDetails.recurrenceId,
                    isRecurring           = initialDetails.isRecurring,
                    recurrenceType        = initialDetails.recurrenceType,
                    updateScope           = initialDetails.updateScope
                )
            } else if (initialRoomName != null) {
                wasPrefilledRef.value = true
                viewModel.preSelectRoom(initialRoomName)
            }
        }
    }

    LaunchedEffect(pendingTimeUpdate) {
        pendingTimeUpdate?.let { (date, start, end) ->
            viewModel.updateTimeOnly(date, start, end)
            onTimeUpdate?.invoke(date, start, end)
        }
    }

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    var isContentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isContentVisible = true }

    val formState = uiState.formState

    var showDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showMeetingTypeDropdown by remember { mutableStateOf(false) }
    var showRecurringDropdown by remember { mutableStateOf(false) }
    // Two separate bottom sheet flags
    var showInternalSheet by remember { mutableStateOf(false) }
    var showExternalSheet by remember { mutableStateOf(false) }
    var showRoomDropdown by remember { mutableStateOf(false) }

    val internalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val externalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val meetingTypes = uiState.availableMeetingTypes.mapNotNull { it.name }.ifEmpty { listOf("Internal", "Client", "Executive") }
    
    val meetingTypeIcons: Map<String, @Composable () -> Unit> = remember(uiState.availableMeetingTypes) {
        if (uiState.availableMeetingTypes.isEmpty()) {
            mapOf(
                "Internal" to { Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFF2196F3))) },
                "Client" to { Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFFFF9800))) },
                "Executive" to { Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color(0xFF9C27B0))) }
            )
        } else {
            uiState.availableMeetingTypes.mapNotNull { type ->
                type.name?.let { name ->
                    name to @Composable {
                        val color = parseMeetingTypeColor(type.colorCode)
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }.toMap()
        }
    }

    // Resolve the colour of the currently-selected meeting type for the trigger field
    val selectedMeetingTypeColor: Color? = remember(formState.meetingType, uiState.availableMeetingTypes) {
        uiState.availableMeetingTypes
            .find { it.name.equals(formState.meetingType, ignoreCase = true) }
            ?.colorCode
            ?.let { parseMeetingTypeColor(it) }
    }

    val recurringOptions = listOf(
        "Does not repeat",
        "Every weekday (Sun-Fri)",
        "Daily",
        "Weekly",
        "Monthly",
        "Yearly",
        "Custom"
    )

    val availableRooms = uiState.availableRooms.map { it.name }

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
                        viewModel.onFormStateChanged(
                            formState.copy(
                                selectedRoom = selected,
                                selectedRoomId = selectedRoom?.id
                            )
                        )
                        showRoomDropdown = false
                    }
                )

                // Room info card when a room is selected
                uiState.availableRooms.find { it.name == formState.selectedRoom }?.let {
                    RoomInfoCard(room = it)
                }

                AnimatedVisibility(visible = formState.recurringType == "Does not repeat") {
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
                }

                // Start / End Time
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
                                        bookingId = formState.bookingId,
                                        roomName = formState.selectedRoom,
                                        date = formState.date,
                                        startTime = formState.startTime,
                                        endTime = formState.endTime,
                                        meetingTitle = formState.meetingTitle,
                                        meetingType = formState.meetingType,
                                        meetingTypeId = formState.meetingTypeId,
                                        description = formState.description,
                                        internalParticipantIds = formState.participants.map { it.id },
                                        externalMembers = formState.externalMembers,
                                        recurrenceId = formState.recurrenceId,
                                        isRecurring = formState.isRecurring,
                                        recurrenceType = formState.recurrenceType,
                                        updateScope = formState.updateScope
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
                                        bookingId = formState.bookingId,
                                        roomName = formState.selectedRoom,
                                        date = formState.date,
                                        startTime = formState.startTime,
                                        endTime = formState.endTime,
                                        meetingTitle = formState.meetingTitle,
                                        meetingType = formState.meetingType,
                                        meetingTypeId = formState.meetingTypeId,
                                        description = formState.description,
                                        internalParticipantIds = formState.participants.map { it.id },
                                        externalMembers = formState.externalMembers,
                                        recurrenceId = formState.recurrenceId,
                                        isRecurring = formState.isRecurring,
                                        recurrenceType = formState.recurrenceType,
                                        updateScope = formState.updateScope
                                    )
                                )
                            }
                        )
                    }
                }

                // Meeting Type
                BookingDropdownField(
                    label = "Meeting Type",
                    value = formState.meetingType,
                    placeholder = "Select meeting type",
                    isRequired = true,
                    expanded = showMeetingTypeDropdown,
                    options = meetingTypes,
                    optionLeadingIcons = meetingTypeIcons,
                    selectedLeadingIcon = selectedMeetingTypeColor?.let { color ->
                        {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    },
                    onExpandChange = { showMeetingTypeDropdown = it },
                    onOptionSelected = { selectedName ->
                        val selectedType = uiState.availableMeetingTypes.find { it.name.equals(selectedName, ignoreCase = true) }
                        viewModel.onFormStateChanged(
                            formState.copy(
                                meetingType = selectedName,
                                meetingTypeId = selectedType?.id
                            )
                        )
                        showMeetingTypeDropdown = false
                    }
                )

                // Recurring
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

                AnimatedVisibility(visible = formState.recurringType == "Custom") {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        listOf("Sun", "Mon", "Tues", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            val isSelected = formState.selectedWeekDays.contains(day)
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF3F4F6))
                                    .clickable {
                                        val newSet = if (isSelected) formState.selectedWeekDays - day else formState.selectedWeekDays + day
                                        viewModel.onFormStateChanged(formState.copy(selectedWeekDays = newSet))
                                    }
                                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) Color.White else Neutral700
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = formState.recurringType != "Does not repeat") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.xs),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.ms)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            BookingClickableField(
                                label = "Start Date",
                                value = formState.date,
                                placeholder = "Start Date",
                                isRequired = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Start Date",
                                        tint = Neutral700,
                                        modifier = Modifier.size(Spacing.lg)
                                    )
                                },
                                onClick = { showDatePicker = true }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            BookingClickableField(
                                label = "End Date",
                                value = formState.recurrenceEndDate,
                                placeholder = "End Date",
                                isRequired = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "End Date",
                                        tint = Neutral700,
                                        modifier = Modifier.size(Spacing.lg)
                                    )
                                },
                                onClick = { showEndDatePicker = true }
                            )
                        }
                    }
                }

                // Description
                BookingTextField(
                    label = "Description",
                    value = formState.description,
                    placeholder = "Add meeting description (optional)",
                    isRequired = false,
                    onValueChange = { viewModel.onFormStateChanged(formState.copy(description = it)) }
                )

                //    Add Member row (Internal + External side-by-side)               
                // Label row
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Add Member",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.customColors.bookRoomLabel
                    )
                    Text(
                        text = " *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val hasAnySelected = formState.participants.isNotEmpty() || formState.externalMembers.isNotEmpty()
                if (hasAnySelected) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        formState.participants.forEach { member ->
                            SelectedParticipantChip(
                                name = member.name,
                                onRemove = {
                                    viewModel.onFormStateChanged(
                                        formState.copy(participants = formState.participants - member)
                                    )
                                }
                            )
                        }
                        formState.externalMembers.forEach { member ->
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.ms)
                ) {
                    // Internal field
                    Box(modifier = Modifier.weight(1f)) {
                        BookingClickableField(
                            label = "",           // label handled above
                            value = "",           // never show names here
                            placeholder = "Add Internal",
                            isRequired = false,   // asterisk shown on the shared label above
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.outline_groups_24),
                                    contentDescription = null,
                                    tint = Neutral700,
                                    modifier = Modifier.size(Spacing.ml)
                                )
                            },
                            onClick = { showInternalSheet = true }
                        )
                    }

                    // External field
                    Box(modifier = Modifier.weight(1f)) {
                        BookingClickableField(
                            label = "",
                            value = "",
                            placeholder = "Add External",
                            isRequired = false,
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.outline_groups_24),
                                    contentDescription = null,
                                    tint = Neutral700,
                                    modifier = Modifier.size(Spacing.ml)
                                )
                            },
                            onClick = { showExternalSheet = true }
                        )
                    }
                }
                //    end Add Member row                                              

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Book button
                PrimaryButton(
                    text = if (uiState.isSubmitting) {
                        if (formState.bookingId != null) "Updating..." else "Booking..."
                    } else {
                        if (formState.bookingId != null) "Update Meeting Room" else "Book Meeting Room"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md),
                    enabled = !uiState.isSubmitting,
                    onClick = { viewModel.onSubmitClicked() }
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

    //    Date picker dialog                                                     
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

    //    End Date picker dialog                                                     
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
                        viewModel.onFormStateChanged(formState.copy(recurrenceEndDate = formatted))
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

    //    Internal participants bottom sheet                                     
    if (showInternalSheet) {
        BottomSheetView(
            onDismiss = { showInternalSheet = false },
            sheetState = internalSheetState,
            title = null
        ) {
            InternalParticipantsBottomSheet(
                formState = formState,
                availableParticipants = uiState.availableParticipants,
                onFormStateChanged = viewModel::onFormStateChanged,
                onClose = { showInternalSheet = false },
                searchQuery = uiState.participantSearchQuery,
                onSearchQueryChange = viewModel::onParticipantSearchQueryChanged,
                isSearching = uiState.isSearchingParticipants
            )
        }
    }

    //    External participants bottom sheet                                     
    if (showExternalSheet) {
        BottomSheetView(
            onDismiss = { showExternalSheet = false },
            sheetState = externalSheetState,
            title = null
        ) {
            ExternalParticipantsBottomSheet(
                formState = formState,
                onFormStateChanged = viewModel::onFormStateChanged,
                onClose = { showExternalSheet = false }
            )
        }
    }

    if (uiState.showRecurrenceScopeDialog) {
        com.swifttechnology.bookingsystem.shared.components.ReusableAlertDialog(
            style = com.swifttechnology.bookingsystem.shared.components.DialogStyle.INFO,
            title = "Update Recurring Meeting",
            message = "Do you want to update just this meeting or all meetings in the series?",
            confirmText = "All occurrences",
            onConfirm = { viewModel.submitWithScope("ALL") },
            dismissText = "This occurrence only",
            onDismiss = { viewModel.submitWithScope("THIS") }
        )
    }
}
