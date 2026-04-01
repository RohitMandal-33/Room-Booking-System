package com.swifttechnology.bookingsystem.features.calendar.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.*
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.*
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
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
@Composable
fun CalendarScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
    pickerViewModel: DayTimePickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val apiRooms by viewModel.rooms.collectAsStateWithLifecycle()
    val pickerUiState by pickerViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    LaunchedEffect(uiState.selectedDate, uiState.events) {
        pickerViewModel.setBlockedSlots(viewModel.getBlockedSlotsForDate(uiState.selectedDate))
    }

    CalendarContent(
        uiState = uiState,
        rooms = apiRooms,
        pickerUiState = pickerUiState,
        onViewChange = viewModel::onViewChange,
        onPrev = viewModel::onPrev,
        onNext = viewModel::onNext,
        onDateSelected = viewModel::onDateSelected,
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
            onNavigate(ScreenRoutes.BOOK_ROOM)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarContent(
    uiState: CalendarUiState,
    rooms: List<Room>,
    pickerUiState: DayPickerUiState,
    onViewChange: (CalendarView) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
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
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.whitePure)
    ) {
        TopBar(
            showRoomPicker = true,
            selectedRoom = uiState.selectedRoom,
            rooms = rooms,
            onRoomSelected = onRoomSelected
        )

        ViewToggleBar(currentView = uiState.currentView, onViewChange = onViewChange)

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
            DayOfWeekHeader(withTimeColumn = false)
        }

        AnimatedContent(
            targetState = uiState.currentView,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "CalendarViewSwitch"
        ) { view ->
            when (view) {
                CalendarView.MONTH -> MonthView(
                    yearMonth = uiState.currentMonth,
                    events = uiState.events,
                    selectedDate = uiState.selectedDate,
                    onDateClick = onDateSelected,
                    onEventClick = { onEventSelected(it) }
                )

                CalendarView.WEEK -> WeekView(
                    selectedDate = uiState.selectedDate,
                    events = uiState.events,
                    onEventClick = { onEventSelected(it) }
                )

                CalendarView.DAY -> Column(modifier = Modifier.fillMaxSize()) {
                    DayColumnWithPicker(
                        selectedDate = uiState.selectedDate,
                        regularEvents = uiState.events,
                        pickerState = pickerUiState,
                        onGridLongPress = onGridLongPress,
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
                            purplePrimary = PurplePrimary,
                            purpleLight = PurpleLight
                        )
                    }
                }
            }
        }
    }

    uiState.selectedEvent?.let { event ->
        MeetingDetailDialog(event = event, onDismiss = { onEventSelected(null) })
    }
}

@Composable
private fun TopBar(
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showRoomPicker) {
            Spacer(Modifier.weight(1f))

            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(CornerRadius.md))
                        .border(1.dp, MaterialTheme.customColors.divider, RoundedCornerShape(CornerRadius.md))
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = selectedRoom?.name ?: "Choose a Meeting Room",
                        color = if (selectedRoom == null) MaterialTheme.customColors.textHint else MaterialTheme.customColors.textPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.customColors.textSecondary
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
        }
    }
}

@Composable
private fun ViewToggleBar(
    currentView: CalendarView,
    onViewChange: (CalendarView) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        CalendarView.values().forEach { view ->
            val isSelected = currentView == view
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onViewChange(view) },
                shape = RoundedCornerShape(CornerRadius.sm),
                color = if (isSelected) PurplePrimary else MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = if (isSelected) Elevation.sm else 0.dp
            ) {
                Text(
                    text = view.name.lowercase().capitalize(Locale.ROOT),
                    modifier = Modifier.padding(vertical = Spacing.xs),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Color.White else MaterialTheme.customColors.textSecondary
                )
            }
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
        Row {
            IconButton(onClick = onPrev) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next")
            }
        }
    }
}

@Composable
fun DayOfWeekHeader(withTimeColumn: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs)
    ) {
        if (withTimeColumn) {
            Spacer(modifier = Modifier.width(50.dp))
        }
        val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.customColors.textSecondary
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthView(
    yearMonth: YearMonth,
    events: List<MeetingEvent>,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (MeetingEvent) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0
    val daysInMonth = yearMonth.lengthOfMonth()
    
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        for (row in 0 until 6) {
            Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - firstDayOfWeek + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayIndex)
                        val isSelected = date == selectedDate
                        val dayEvents = events.filter { it.date == date }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(0.5.dp, MaterialTheme.customColors.divider)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { onDateClick(date) }
                                .padding(2.dp)
                        ) {
                            Column {
                                Text(
                                    text = dayIndex.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PurplePrimary else MaterialTheme.customColors.textPrimary
                                )
                                dayEvents.take(3).forEach { event ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 1.dp)
                                            .background(PurplePrimary.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                                            .padding(horizontal = 2.dp)
                                            .clickable { onEventClick(event) }
                                    ) {
                                        Text(
                                            text = event.title,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = PurplePrimary
                                        )
                                    }
                                }
                                if (dayEvents.size > 3) {
                                    Text(
                                        text = "+${dayEvents.size - 3} more",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                        color = MaterialTheme.customColors.textSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, MaterialTheme.customColors.divider))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekView(
    selectedDate: LocalDate,
    events: List<MeetingEvent>,
    onEventClick: (MeetingEvent) -> Unit
) {
    val startOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    
    Column(modifier = Modifier.fillMaxSize()) {
        DayOfWeekHeader(withTimeColumn = true)
        
        Row(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Column(modifier = Modifier.width(50.dp)) {
                for (hour in 0..23) {
                    Box(modifier = Modifier.height(60.dp).padding(start = 8.dp)) {
                        Text(
                            text = String.format("%02d:00", hour),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.customColors.textSecondary
                        )
                    }
                }
            }
            
            for (i in 0..6) {
                val date = startOfWeek.plusDays(i.toLong())
                val dayEvents = events.filter { it.date == date }
                
                Column(modifier = Modifier.weight(1f).border(0.5.dp, MaterialTheme.customColors.divider)) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column {
                            for (hour in 0..23) {
                                Box(modifier = Modifier.fillMaxWidth().height(60.dp).border(0.2.dp, MaterialTheme.customColors.divider.copy(alpha = 0.5f)))
                            }
                        }
                        
                        dayEvents.forEach { event ->
                            val startMinutes = event.startTime.hour * 60 + event.startTime.minute
                            val endMinutes = event.endTime.hour * 60 + event.endTime.minute
                            val duration = endMinutes - startMinutes
                            
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 1.dp)
                                    .fillMaxWidth()
                                    .height((duration * 1.0).dp)
                                    .offset(y = (startMinutes * 1.0).dp)
                                    .background(PurplePrimary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                    .border(1.dp, PurplePrimary, RoundedCornerShape(4.dp))
                                    .clickable { onEventClick(event) }
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MeetingDetailDialog(
    event: MeetingEvent,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Meeting details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(PurplePrimary))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Reserved", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                DetailItem(
                    icon = Icons.Outlined.Schedule,
                    title = event.date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                    subtitle = "${event.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))} - ${event.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))}"
                )

                DetailItem(
                    icon = Icons.Outlined.Place,
                    title = if(event.meetingRoom.isNotEmpty()) event.meetingRoom else "Unassigned",
                    subtitle = "Meeting Room"
                )

                DetailItem(
                    icon = Icons.Outlined.AccountCircle,
                    title = "Organized by",
                    subtitle = if(event.createdBy.isNotEmpty()) event.createdBy else "Unknown"
                )

                DetailItem(
                    icon = Icons.Outlined.Group,
                    title = "Attendees",
                    subtitle = "${event.participants.size} people"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun DetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
