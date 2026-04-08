package com.swifttechnology.bookingsystem.features.booking.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.dayview.DayColumnWithPicker
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.DayStrip
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.DayTimePickerViewModel
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.IntervalUtils
import com.swifttechnology.bookingsystem.features.calendar.presentation.CalendarViewModel
import com.swifttechnology.bookingsystem.features.main.presentation.PendingBookingDetails
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.CalendarLegendRow
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.dayview.TimeBox
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.swifttechnology.bookingsystem.shared.components.TimePickerBottomSheet

private val PurplePrimary  = Color(0xFF6C3EE8)
private val PurpleLight    = Color(0xFFEDE9FF)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoomCalendarScreen(
    roomName: String?,
    initialDetails: PendingBookingDetails?,
    onNavigateBack: () -> Unit,
    onProceed: (PendingBookingDetails) -> Unit,
    pickerViewModel: DayTimePickerViewModel = hiltViewModel(key = "room_calendar_picker"),
    calendarViewModel: CalendarViewModel = hiltViewModel()
) {
    val pickerUiState by pickerViewModel.uiState.collectAsStateWithLifecycle()
    val calendarUiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedDate by remember {
        mutableStateOf(
            try {
                if (initialDetails?.date != null) {
                    LocalDate.parse(
                        initialDetails.date,
                        DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.getDefault())
                    )
                } else {
                    LocalDate.now()
                }
            } catch (e: Exception) {
                LocalDate.now()
            }
        )
    }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker   by remember { mutableStateOf(false) }

    val draggable = pickerUiState.draggableEvent

    LaunchedEffect(initialDetails) {
        if (initialDetails != null) {
            try {
                val startParts = initialDetails.startTime.split(":")
                val endParts   = initialDetails.endTime.split(":")
                val startMin   = startParts[0].toInt() * 60 + startParts[1].toInt()
                val endMin     = endParts[0].toInt() * 60 + endParts[1].toInt()
                pickerViewModel.initializePicker(startMin, endMin)
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(calendarUiState.events, selectedDate, roomName) {
        if (roomName != null) {
            pickerViewModel.setBlockedSlots(
                calendarViewModel.getBlockedSlotsForRoomAndDate(roomName, selectedDate)
            )
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxSize()) {

            //    Top bar                                                   
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calendar grid icon (purple) — fixed size, won't grow
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))

                // Room name — takes remaining space, truncates, never pushes close off screen
                Text(
                    text = roomName ?: "Select Time",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Close button — fixed, always visible
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF555555)
                    )
                }
            }

            // Subtitle — single line, ellipsis if needed
            Text(
                text = "Tap then Drag across time slots to select your booking duration",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF888888),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = Spacing.md)
                    .padding(bottom = Spacing.sm)
            )

            //    Week strip                                                 
            DayStrip(
                selectedDate = selectedDate,
                onDateClick = { selectedDate = it },
                onPreviousWeek = { selectedDate = selectedDate.minusWeeks(1) },
                onNextWeek = { selectedDate = selectedDate.plusWeeks(1) }
            )

            //    Time picking grid (fills remaining space)                  
            DayColumnWithPicker(
                selectedDate = selectedDate,
                regularEvents = roomName?.let {
                    calendarViewModel.getEventsForRoomAndDate(it, selectedDate)
                } ?: emptyList(),
                pickerState = pickerUiState,
                onGridLongPress = pickerViewModel::onTimeSlotLongPressed,
                onDragStarted = pickerViewModel::onDragStarted,
                onPreviewChanged = pickerViewModel::onPreviewChanged,
                onMoveCommitted = pickerViewModel::onMoveCommitted,
                onResizeTopCommitted = pickerViewModel::onResizeTopCommitted,
                onResizeBottomCommitted = pickerViewModel::onResizeBottomCommitted,
                onDragCancelled = pickerViewModel::onDragCancelled,
                modifier = Modifier.weight(1f)
            )

            //    Legend                                                     
            CalendarLegendRow()

            //    Bottom: Selected Time Slot card — visible only when a picker block exists   
            if (draggable != null) {
                val range      = draggable.timeRange
                val startLabel = IntervalUtils.formatMinutes(range.startMinutes)
                val endLabel   = IntervalUtils.formatMinutes(range.endMinutes)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, PurplePrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .background(PurpleLight)
                        .padding(horizontal = Spacing.md, vertical = Spacing.md)
                ) {
                    Column {
                        Text(
                            text = "Selected Time Slot:",
                            fontSize = 12.sp,
                            color = PurplePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Start time box — tapping opens spinner time picker
                            TimeBox(
                                time = startLabel,
                                onClick = { showStartTimePicker = true }
                            )

                            Spacer(modifier = Modifier.width(Spacing.sm))
                            Text(
                                text = "-",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.width(Spacing.sm))

                            // End time box — tapping opens spinner time picker
                            TimeBox(
                                time = endLabel,
                                onClick = { showEndTimePicker = true }
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // Proceed button
                            PrimaryButton(
                                text = "Proceed",
                                modifier = Modifier.width(110.dp),
                                onClick = {
                                    val startH = range.startMinutes / 60
                                    val startM = range.startMinutes % 60
                                    val endH   = range.endMinutes   / 60
                                    val endM   = range.endMinutes   % 60

                                    val formattedDate = java.text.SimpleDateFormat(
                                        "dd MMM yyyy", java.util.Locale.getDefault()
                                    ).format(
                                        java.util.Date.from(
                                            selectedDate.atStartOfDay(
                                                java.time.ZoneId.systemDefault()
                                            ).toInstant()
                                        )
                                    )

                                    onProceed(
                                        PendingBookingDetails(
                                            roomName  = roomName ?: "",
                                            date      = formattedDate,
                                            startTime = "%02d:%02d".format(startH, startM),
                                            endTime   = "%02d:%02d".format(endH, endM)
                                        )
                                    )
                                    pickerViewModel.onCancelBooking()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showStartTimePicker && draggable != null) {
        val range = draggable.timeRange
        TimePickerBottomSheet(
            title = "Set start time",
            initialHour = range.startMinutes / 60,
            initialMinute = range.startMinutes % 60,
            onConfirm = { h, m ->
                val newStart = h * 60 + m
                val newEnd = (newStart + range.durationMinutes).coerceAtMost(24 * 60)
                pickerViewModel.initializePicker(newStart, newEnd)
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker && draggable != null) {
        val range = draggable.timeRange
        TimePickerBottomSheet(
            title = "Set end time",
            initialHour = range.endMinutes / 60,
            initialMinute = range.endMinutes % 60,
            onConfirm = { h, m ->
                val newEnd = h * 60 + m
                pickerViewModel.initializePicker(
                    range.startMinutes,
                    newEnd.coerceAtLeast(range.startMinutes + 5)
                )
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}
