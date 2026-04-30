package com.swifttechnology.bookingsystem.features.calendar.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.*
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.features.booking.data.dtos.ExternalParticipantDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.InternalParticipantDTO
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.dayview.*
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.monthview.*
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.*
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.weekview.*
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.TimePickerBottomSheet
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

private val PurplePrimary = Color(0xFF6C3EE8)
private val PurpleLight = Color(0xFFEDE9FF)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(

    searchQuery: String,
    onNavigate: (String) -> Unit,
    onProceedWithDetails: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onEditMeeting: (MeetingEvent, String) -> Unit = { _, _ -> },
    todayTrigger: Int = 0,
    viewModel: CalendarViewModel = hiltViewModel(),
    pickerViewModel: DayTimePickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val apiRooms by viewModel.rooms.collectAsStateWithLifecycle()
    val pickerUiState by pickerViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    LaunchedEffect(todayTrigger) {
        if (todayTrigger > 0) {
            viewModel.onDateSelected(LocalDate.now())
        }
    }

    LaunchedEffect(uiState.selectedDate, uiState.selectedRoom, uiState.currentView) {
        val room = uiState.selectedRoom
        val apiBlockedSlots = if (room != null) {
            viewModel.getBlockedSlotsForRoomAndDate(room.name, uiState.selectedDate)
        } else {
            viewModel.getBlockedSlotsForDate(uiState.selectedDate)
        }

        // Add past time as a blocked area
        val now = LocalDate.now()
        val pastBlocks = mutableListOf<TimeRange>()
        if (uiState.selectedDate.isBefore(now)) {
            // Entire past day is blocked
            pastBlocks.add(TimeRange(0, 24 * 60))
        } else if (uiState.selectedDate.isEqual(now)) {
            // Block from midnight until now
            val currentMinutes = LocalTime.now().let { it.hour * 60 + it.minute }
            if (currentMinutes > 0) {
                pastBlocks.add(TimeRange(0, currentMinutes))
            }
        }

        pickerViewModel.setBlockedSlots(apiBlockedSlots, pastBlocks)
        // Reset picker when date, room, or view mode changes
        pickerViewModel.onCancelBooking()
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshBookings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    CalendarContent(
        uiState = uiState,
        rooms = apiRooms,
        pickerUiState = pickerUiState,
        onViewChange = viewModel::onViewChange,
        onPrev = viewModel::onPrev,
        onNext = viewModel::onNext,
        onDateSelected = viewModel::onDateSelected,
        onMonthTileClick = { date ->
            if (uiState.selectedDate == date) {
                viewModel.onViewChange(CalendarView.DAY)
            } else {
                viewModel.onDateSelected(date)
            }
        },


        onEventSelected = viewModel::onEventSelected,
        onRoomSelected = viewModel::onRoomSelected,
        onGridLongPress = pickerViewModel::onTimeSlotLongPressed,
        onPickerDragStarted = pickerViewModel::onDragStarted,
        onPickerPreviewChanged = pickerViewModel::onPreviewChanged,
        onPickerMoveCommitted = pickerViewModel::onMoveCommitted,
        onPickerResizeTopCommitted = pickerViewModel::onResizeTopCommitted,
        onPickerResizeBottomCommitted = pickerViewModel::onResizeBottomCommitted,
        onPickerDragCancelled = pickerViewModel::onDragCancelled,
        onPickerCancelBooking = pickerViewModel::onCancelBooking,
        onProceed = {
            val draggable = pickerUiState.draggableEvent
            if (draggable != null) {
                val roomName = uiState.selectedRoom?.name ?: ""
                val dateStr = uiState.selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
                val startTime = IntervalUtils.formatMinutes(draggable.timeRange.startMinutes)
                val endTime   = IntervalUtils.formatMinutes(draggable.timeRange.endMinutes)
                onProceedWithDetails(roomName, dateStr, startTime, endTime)
            } else {
                onNavigate(ScreenRoutes.BOOK_ROOM)
            }
        },
        onTimePickerConfirm = pickerViewModel::initializePicker,
        onEditMeeting = { event, scope ->
            onEditMeeting(event, scope)
            viewModel.onEventSelected(null)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarContent(
    uiState: CalendarUiState,
    rooms: List<Room>,
    pickerUiState: DayPickerUiState,
    onViewChange: (CalendarView) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onMonthTileClick: (LocalDate) -> Unit,
    onEventSelected: (MeetingEvent?) -> Unit,
    onRoomSelected: (Room) -> Unit,
    onGridLongPress: (Int) -> Unit,
    onPickerDragStarted: () -> Unit,
    onPickerPreviewChanged: (TimeRange) -> Unit,
    onPickerMoveCommitted: (Int) -> Unit,
    onPickerResizeTopCommitted: (Int) -> Unit,
    onPickerResizeBottomCommitted: (Int) -> Unit,
    onPickerDragCancelled: () -> Unit,
    onPickerCancelBooking: () -> Unit,
    onProceed: () -> Unit,
    onTimePickerConfirm: (Int, Int) -> Unit,
    onEditMeeting: (MeetingEvent, String) -> Unit = { _, _ -> }
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker   by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.whitePure)
    ) {
        TopBar(
            currentView = uiState.currentView,
            onViewChange = onViewChange,
            showRoomPicker = true,
            selectedRoom = uiState.selectedRoom,
            rooms = rooms,
            onRoomSelected = onRoomSelected
        )

        NavigationHeader(
            currentView = uiState.currentView,
            currentMonth = uiState.currentMonth,
            selectedDate = uiState.selectedDate,
            onPrev = onPrev,
            onNext = onNext
        )

        if (uiState.currentView == CalendarView.DAY) {
            DayStrip(
                selectedDate = uiState.selectedDate,
                onDateClick = onDateSelected
            )
        }

        if (uiState.currentView == CalendarView.MONTH) {
            val isCurrentMonth = uiState.currentMonth == YearMonth.now()
            DayOfWeekHeader(withTimeColumn = false, isCurrentMonth = isCurrentMonth)
        }

        val room = uiState.selectedRoom
        val filteredEvents = remember(uiState.events, room) {
            if (room != null) {
                uiState.events.filter { it.meetingRoom.equals(room.name, ignoreCase = true) }
            } else {
                uiState.events
            }
        }

        AnimatedContent(
            targetState = uiState.currentView,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "CalendarViewSwitch"
        ) { view ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(view) {
                        var dragAccumulator = 0f
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragAccumulator > 150f) {
                                    onPrev()
                                } else if (dragAccumulator < -150f) {
                                    onNext()
                                }
                                dragAccumulator = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                dragAccumulator += dragAmount
                            }
                        )
                    }
            ) {
                when (view) {
                CalendarView.MONTH -> MonthView(
                    yearMonth = uiState.currentMonth,
                    events = filteredEvents,
                    selectedDate = uiState.selectedDate,
                    onDateClick = onMonthTileClick,
                    onEventClick = { onEventSelected(it) }
                )

                CalendarView.WEEK -> WeekView(
                    selectedDate = uiState.selectedDate,
                    events = filteredEvents,
                    onDateClick = { onDateSelected(it) },
                    onEventClick = { onEventSelected(it) }
                )

                CalendarView.DAY -> Column(modifier = Modifier.fillMaxSize()) {
                    DayColumnWithPicker(
                        selectedDate = uiState.selectedDate,
                        regularEvents = filteredEvents,
                        pickerState = pickerUiState,
                        onGridLongPress = onGridLongPress,
                        onEventClick = { onEventSelected(it) },
                        onDragStarted = onPickerDragStarted,
                        onPreviewChanged = onPickerPreviewChanged,
                        onMoveCommitted = onPickerMoveCommitted,
                        onResizeTopCommitted = onPickerResizeTopCommitted,
                        onResizeBottomCommitted = onPickerResizeBottomCommitted,
                        onDragCancelled = onPickerDragCancelled,
                        modifier = Modifier.weight(1f)
                    )
                    
                    CalendarLegendRow()

                    if (pickerUiState.draggableEvent != null) {
                        CalendarBottomBar(
                            pickerState = pickerUiState,
                            onCancel = onPickerCancelBooking,
                            onProceed = onProceed,
                            onStartTimeClick = { showStartPicker = true },
                            onEndTimeClick = { showEndPicker = true },
                            purplePrimary = PurplePrimary,
                            purpleLight = PurpleLight
                        )
                    }
                }
            }
            }
        }
    }

    if (showStartPicker) {
        val range = pickerUiState.draggableEvent?.timeRange
        if (range != null) {
            TimePickerBottomSheet(
                title = "Set start time",
                initialHour = range.startMinutes / 60,
                initialMinute = range.startMinutes % 60,
                onConfirm = { h, m ->
                    val newStart = h * 60 + m
                    val newEnd = (newStart + range.durationMinutes).coerceAtMost(24 * 60)
                    onTimePickerConfirm(newStart, newEnd)
                },
                onDismiss = { showStartPicker = false }
            )
        }
    }

    if (showEndPicker) {
        val range = pickerUiState.draggableEvent?.timeRange
        if (range != null) {
            TimePickerBottomSheet(
                title = "Set end time",
                initialHour = range.endMinutes / 60,
                initialMinute = range.endMinutes % 60,
                onConfirm = { h, m ->
                    val newEnd = h * 60 + m
                    val newStart = range.startMinutes
                    if (newEnd > newStart) {
                        onTimePickerConfirm(newStart, newEnd)
                    }
                },
                onDismiss = { showEndPicker = false }
            )
        }
    }

    uiState.selectedEvent?.let { event ->
        MeetingDetailBottomSheet(
            event = event,
            onDismiss = { onEventSelected(null) },
            onEdit = { scope -> onEditMeeting(event, scope) }
        )
    }
}

@Composable
private fun TopBar(
    currentView: CalendarView,
    onViewChange: (CalendarView) -> Unit,
    showRoomPicker: Boolean,
    selectedRoom: Room?,
    rooms: List<Room>,
    onRoomSelected: (Room) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CalendarView.values().forEach { view ->
                val isSelected = currentView == view
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) PurplePrimary else Color.Transparent)
                        .clickable { onViewChange(view) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = view.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.customColors.textSecondary
                    )
                }
            }
        }

        if (showRoomPicker) {
            Box {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFF9D74FE), CircleShape)
                        .background(Color.White)
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp)
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = selectedRoom?.name ?: "Meeting Room",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.customColors.textPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select room",
                        tint = MaterialTheme.customColors.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    rooms.forEach { room ->
                        DropdownMenuItem(
                            text = { Text(room.name) },
                            onClick = {
                                onRoomSelected(room)
                                expanded = false
                            }
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun NavigationHeader(
    currentView: CalendarView,
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val title = when (currentView) {
        CalendarView.MONTH -> currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        CalendarView.WEEK -> {
            val start = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val end = start.plusDays(6)
            if (start.month == end.month) {
                "${start.format(DateTimeFormatter.ofPattern("MMM"))} ${start.dayOfMonth} - ${end.dayOfMonth}, ${start.year}"
            } else {
                "${start.format(DateTimeFormatter.ofPattern("MMM d"))} - ${end.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}"
            }
        }
        CalendarView.DAY -> selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.customColors.textPrimary
        )

    }
}

@Composable
fun DayOfWeekHeader(withTimeColumn: Boolean = false, isCurrentMonth: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs)
    ) {
        if (withTimeColumn) {
            Spacer(modifier = Modifier.width(50.dp))
        }
        val days = listOf("SUN", "MON", "TUE", "WED", "THUR", "FRI", "SAT")
        val todayStr = java.time.LocalDate.now().dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH).uppercase()

        days.forEach { day ->
            val isToday = isCurrentMonth && day.take(3) == todayStr.take(3)
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isToday) PurplePrimary else MaterialTheme.customColors.textSecondary
            )
        }
    }
}

