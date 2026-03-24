package com.swifttechnology.bookingsystem.features.booking.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents.DayColumnWithPicker
import com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents.DayStrip
import com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents.DayTimePickerViewModel
import com.swifttechnology.bookingsystem.features.main.presentation.PendingBookingDetails
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RoomCalendarScreen(
    roomName: String?,
    initialDetails: PendingBookingDetails?,
    onNavigateBack: () -> Unit,
    onProceed: (PendingBookingDetails) -> Unit,
    pickerViewModel: DayTimePickerViewModel = hiltViewModel(key = "room_calendar_picker")
) {
    val pickerUiState by pickerViewModel.uiState.collectAsStateWithLifecycle()

    var selectedDate by remember {
        mutableStateOf(
            try {
                if (initialDetails?.date != null) {
                    LocalDate.parse(initialDetails.date, DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.getDefault()))
                } else {
                    LocalDate.now()
                }
            } catch (e: Exception) {
                LocalDate.now()
            }
        )
    }

    LaunchedEffect(initialDetails) {
        if (initialDetails?.startTime != null && initialDetails.endTime != null) {
            try {
                val startParts = initialDetails.startTime.split(":")
                val endParts = initialDetails.endTime.split(":")
                val startMin = startParts[0].toInt() * 60 + startParts[1].toInt()
                val endMin = endParts[0].toInt() * 60 + endParts[1].toInt()
                pickerViewModel.initializePicker(startMin, endMin)
            } catch (e: Exception) {
                // Ignore parse errors
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = roomName ?: "Select Time",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Date Strip
            DayStrip(
                selectedDate = selectedDate,
                onDateClick = { selectedDate = it }
            )

            // Time Grid Picker
            Column(modifier = Modifier.weight(1f)) {
                DayColumnWithPicker(
                    selectedDate = selectedDate,
                    regularEvents = emptyList(), // TODO: Pass any known events for this room
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

                // Bottom Proceed Area
                if (pickerUiState.draggableEvent != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PrimaryButton(
                            text = "Proceed",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val range = pickerUiState.draggableEvent!!.timeRange
                                val startH = range.startMinutes / 60
                                val startM = range.startMinutes % 60
                                val endH = range.endMinutes / 60
                                val endM = range.endMinutes % 60

                                val formattedDate = java.text.SimpleDateFormat(
                                    "dd MMM yyyy", java.util.Locale.getDefault()
                                ).format(
                                    java.util.Date.from(
                                        selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()
                                    )
                                )

                                onProceed(
                                    PendingBookingDetails(
                                        roomName = roomName ?: "",
                                        date = formattedDate,
                                        startTime = "%02d:%02d".format(startH, startM),
                                        endTime = "%02d:%02d".format(endH, endM)
                                    )
                                )
                                // Clear picker state for next usage
                                pickerViewModel.onCancelBooking()
                            }
                        )
                    }
                }
            }
        }
    }
}
