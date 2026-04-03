package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.monthview

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import java.time.LocalDate
import java.time.YearMonth

private val PurplePrimary = Color(0xFF6C3EE8)
private val MaxVisibleEvents = 3

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthView(
    yearMonth: YearMonth,
    events: List<MeetingEvent>,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (MeetingEvent) -> Unit
) {
    val today = LocalDate.now()
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0
    val daysInMonth = yearMonth.lengthOfMonth()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        for (row in 0 until 6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - firstDayOfWeek + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayIndex)
                        val isSelected = date == selectedDate
                        val isToday = date == today
                        val dayEvents = events.filter { it.date == date }

                        MonthDayTile(
                            dayNumber = dayIndex,
                            isSelected = isSelected,
                            isToday = isToday,
                            events = dayEvents,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = { onDateClick(date) },
                            onEventClick = onEventClick
                        )
                    } else {
                        // Empty cell for days outside this month
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(0.5.dp, MaterialTheme.customColors.divider)
                        ) {
                            // Show overflow day number in muted grey
                            val overflowDay = if (dayIndex < 1) {
                                val prevMonth = yearMonth.minusMonths(1)
                                prevMonth.lengthOfMonth() + dayIndex
                            } else {
                                dayIndex - daysInMonth
                            }
                            Text(
                                text = overflowDay.toString(),
                                modifier = Modifier.padding(4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.customColors.textSecondary.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthDayTile(
    dayNumber: Int,
    isSelected: Boolean,
    isToday: Boolean,
    events: List<MeetingEvent>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEventClick: (MeetingEvent) -> Unit
) {
    val visibleEvents = events.take(MaxVisibleEvents)
    val overflowCount = events.size - visibleEvents.size

    Box(
        modifier = modifier
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) PurplePrimary else MaterialTheme.customColors.divider,
                shape = if (isSelected) RoundedCornerShape(0.dp) else RoundedCornerShape(0.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 3.dp, vertical = 3.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            // Day number badge
            Row(modifier = Modifier.fillMaxWidth()) {
                if (isToday) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = dayNumber.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) PurplePrimary else MaterialTheme.customColors.textPrimary,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            // Event pills
            visibleEvents.forEach { event ->
                EventPill(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }

            // Overflow chip
            if (overflowCount > 0) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .border(1.dp, MaterialTheme.customColors.divider, RoundedCornerShape(50))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.customColors.textSecondary
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventPill(
    event: MeetingEvent,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(50))
            .background(event.color.copy(alpha = 0.85f))
            .clickable(onClick = onClick)
            .padding(horizontal = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Intentionally left blank — pills are color-only indicators like the reference image
    }
}
