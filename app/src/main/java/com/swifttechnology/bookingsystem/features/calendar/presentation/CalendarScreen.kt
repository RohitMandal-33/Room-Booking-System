package com.swifttechnology.bookingsystem.features.calendar.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.shadow
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.IntervalUtils
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation
import com.swifttechnology.bookingsystem.core.designsystem.customColors
// rooms are now provided by CalendarViewModel
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.DayColumnWithPicker
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.DayTimePickerViewModel
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.DayPickerUiState
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.TimeRange
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.DayStrip
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes

import androidx.compose.runtime.LaunchedEffect

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
        onPickerCancelBooking = pickerViewModel::onCancelBooking
    )
}

//Calendar Content Root

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
    onPickerCancelBooking: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.whitePure)
    ) {
        TopBar(
            currentView = uiState.currentView,
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
                    
                    if (pickerUiState.draggableEvent != null) {
                        DayBookingBottomBar(
                            pickerState = pickerUiState,
                            selectedDate = uiState.selectedDate,
                            onCancel = onPickerCancelBooking,
                            onProceed = { /* Handle navigate to booking screen later */ }
                        )
                    }
                }
            }
        }
    }

    // Dialog is kept outside the Column so it renders as a proper overlay.
    uiState.selectedEvent?.let { event ->
        MeetingDetailDialog(event = event, onDismiss = { onEventSelected(null) })
    }
}


private val PurplePrimary = Color(0xFF9B6DFF)

@Composable
private fun TopBar(
    currentView: CalendarView,
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
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFF777777),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    rooms.forEach { room ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    room.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            onClick = {
                                onRoomSelected(room)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

//View Toggle

@Composable
private fun ViewToggleBar(currentView: CalendarView, onViewChange: (CalendarView) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(
            CalendarView.MONTH to "Month",
            CalendarView.WEEK to "Week",
            CalendarView.DAY to "Day"
        ).forEach { (view, label) ->
            val selected = currentView == view
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.full))
                    .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onViewChange(view) }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (selected) Color.White else MaterialTheme.customColors.textPrimary,
                    style = if (selected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
                )
            }
        }
    }
}

// Navigation Header

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
        CalendarView.MONTH -> currentMonth.format(DateTimeFormatter.ofPattern("MMM yyyy"))
        CalendarView.WEEK, CalendarView.DAY -> {
            val weekStart =
                selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            weekStart.format(DateTimeFormatter.ofPattern("MMM yyyy"))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.sm, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrev) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = "Previous",
                tint = MaterialTheme.customColors.textPrimary
            )
        }
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.customColors.textPrimary)
        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Next",
                tint = MaterialTheme.customColors.textPrimary
            )
        }
    }
}

// Day of Week Header 

@Composable
private fun DayOfWeekHeader(withTimeColumn: Boolean) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.customColors.surfaceLight)
            .padding(vertical = 6.dp)
    ) {
        if (withTimeColumn) Spacer(Modifier.width(56.dp))
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

// Month View

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthView(
    yearMonth: YearMonth,
    events: List<MeetingEvent>,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (MeetingEvent) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    val firstDayOfWeek = DayOfWeek.MONDAY
    val startOffset = (firstDay.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val daysInMonth = yearMonth.lengthOfMonth()
    val prevMonth = yearMonth.minusMonths(1)
    val prevMonthDays = prevMonth.lengthOfMonth()
    val totalCells = 42 // 6 rows × 7 cols
    val today = LocalDate.now()
    val eventsByDate = remember(events) { events.groupBy { it.date } }

    Column(modifier = Modifier.fillMaxWidth()) {
        val cells = (0 until totalCells).map { idx ->
            when {
                idx < startOffset ->
                    prevMonth.atDay(prevMonthDays - startOffset + idx + 1) to false

                idx < startOffset + daysInMonth ->
                    yearMonth.atDay(idx - startOffset + 1) to true

                else ->
                    yearMonth.plusMonths(1).atDay(idx - startOffset - daysInMonth + 1) to false
            }
        }

        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 80.dp)
            ) {
                week.forEach { (date, inMonth) ->
                    val dayEvents = eventsByDate[date].orEmpty()
                    val isSelected = date == selectedDate
                    val isToday = date == today
                    MonthCell(
                        date = date,
                        inMonth = inMonth,
                        events = dayEvents,
                        isSelected = isSelected,
                        isToday = isToday,
                        modifier = Modifier.weight(1f),
                        onClick = { onDateClick(date) },
                        onEventClick = onEventClick
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.customColors.divider, thickness = 0.5.dp)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthCell(
    date: LocalDate,
    inMonth: Boolean,
    events: List<MeetingEvent>,
    isSelected: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEventClick: (MeetingEvent) -> Unit
) {
    val cellShape = RoundedCornerShape(CornerRadius.md)
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 80.dp)
            .clip(cellShape)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                    else -> MaterialTheme.customColors.divider
                },
                shape = cellShape
            )
            .clickable(onClick = onClick)
            .padding(2.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(start = 4.dp, top = 4.dp)
                .size(26.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    isSelected -> Color.White
                    !inMonth -> MaterialTheme.customColors.textHint
                    else -> MaterialTheme.customColors.textPrimary
                },
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
        Spacer(Modifier.height(2.dp))
        events.take(2).forEach { event ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 1.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(event.color)
                    .clickable { onEventClick(event) }
                    .padding(horizontal = 3.dp, vertical = 2.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        val remaining = events.size - 2
        if (remaining > 0) {
            Text(
                text = "+$remaining more",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.customColors.textSecondary,
                modifier = Modifier.padding(start = 6.dp, top = 2.dp)
            )
        }
    }
}

// Week View 

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekView(
    selectedDate: LocalDate,
    events: List<MeetingEvent>,
    onEventClick: (MeetingEvent) -> Unit
) {
    val weekStart = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val weekDays = (0..6).map { weekStart.plusDays(it.toLong()) }
    val hours = 0..23

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.customColors.surfaceLight)
        ) {
            Spacer(Modifier.width(56.dp))
            weekDays.forEach { day ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.customColors.textSecondary
                    )
                    Text(
                        text = day.dayOfMonth.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.customColors.textPrimary
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.customColors.divider)

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            hours.forEach { hour ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .padding(end = 8.dp, top = 2.dp)
                    ) {
                        Text(
                            text = formatHour(hour),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.customColors.textHint,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    weekDays.forEach { day ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.customColors.divider
                                )
                        ) {
                            val dayEvents = events.filter {
                                it.date == day && it.startTime.hour == hour
                            }
                            dayEvents.forEach { event ->
                                val durationMins =
                                    java.time.Duration.between(event.startTime, event.endTime)
                                        .toMinutes()
                                val heightFraction = (durationMins / 60f).coerceAtMost(1f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(heightFraction)
                                        .padding(1.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(event.color)
                                        .clickable { onEventClick(event) }
                                        .padding(3.dp)
                                ) {
                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.customColors.dividerLight, thickness = 0.5.dp)
            }
        }
    }
}

// Meeting Detail Dialog

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MeetingDetailDialog(event: MeetingEvent, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(CornerRadius.lg),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.customColors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                DialogRow(icon = {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.customColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }) {
                    Column {
                        val dateStr =
                            event.date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM"))
                        val timeStr =
                            "${event.startTime.format(DateTimeFormatter.ofPattern("h:mma"))} - ${
                                event.endTime.format(DateTimeFormatter.ofPattern("h:mma"))
                            }"
                        Text(
                            "$dateStr    $timeStr",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${event.timeZone} . ${if (event.repeats) "Repeats" else "Doesn't repeat"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.customColors.textSecondary
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                DialogRow(icon = {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = MaterialTheme.customColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }) {
                    Text("Participants", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(12.dp))

                DialogRow(icon = {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = null,
                        tint = MaterialTheme.customColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }) {
                    Column {
                        Text("Description", style = MaterialTheme.typography.bodyMedium)
                        if (event.description.isNotBlank()) {
                            Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.customColors.textSecondary)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                DialogRow(icon = {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        tint = MaterialTheme.customColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }) {
                    Text("Meeting room", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(12.dp))

                DialogRow(icon = {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.customColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }) {
                    Text("Created By", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

//Dialog Row

@Composable
private fun DialogRow(icon: @Composable () -> Unit, content: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.ms)
    ) {
        Box(modifier = Modifier.padding(top = 2.dp)) { icon() }
        Box(modifier = Modifier.weight(1f)) { content() }
    }
}

// Utils

private fun formatHour(hour: Int): String {
    return when (hour) {
        0 -> "12:00 am"
        in 1..11 -> "$hour:00 am"
        12 -> "12:00 pm"
        else -> "${hour - 12}:00 pm"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayBookingBottomBar(
    pickerState: DayPickerUiState,
    selectedDate: LocalDate,
    onCancel: () -> Unit,
    onProceed: () -> Unit
) {
    if (pickerState.draggableEvent == null) return

    val event = pickerState.draggableEvent
    val startMin = event.timeRange.startMinutes
    val endMin = event.timeRange.endMinutes

    val startTime = LocalTime.of(startMin / 60, startMin % 60)
    val endTime = LocalTime.of(endMin / 60, endMin % 60)

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val startTimeStr = startTime.format(timeFormatter)
    val endTimeStr = endTime.format(timeFormatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = Elevation.xl,
                shape = RoundedCornerShape(topStart = CornerRadius.xl, topEnd = CornerRadius.xl),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(topStart = CornerRadius.xl, topEnd = CornerRadius.xl))
            .background(Color.White)
            .padding(Spacing.ml)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.customColors.primaryLight, RoundedCornerShape(CornerRadius.md))
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Selected Time",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.customColors.textSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$startTimeStr - $endTimeStr",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = IntervalUtils.formatDuration(event.timeRange.durationMinutes),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.customColors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(Spacing.ml))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(CornerRadius.md),
                border = BorderStroke(1.dp, MaterialTheme.customColors.divider)
            ) {
                Text("Cancel", color = MaterialTheme.customColors.textPrimary, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
            }

            Button(
                onClick = onProceed,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(CornerRadius.md),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed to booking", color = Color.White, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                Spacer(Modifier.width(Spacing.sm))
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}