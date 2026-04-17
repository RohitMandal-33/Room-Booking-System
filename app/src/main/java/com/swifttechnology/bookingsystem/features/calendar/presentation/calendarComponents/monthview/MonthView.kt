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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import java.time.LocalDate
import java.time.YearMonth

private val PurplePrimary = Color(0xFF6C3EE8)
private val MaxVisibleEvents = 4
private val TileCornerRadius = 10.dp

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
            .background(Color.White)
    ) {
        for (row in 0 until 6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
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
                                .fillMaxHeight()
                                .padding(2.dp),
                            onClick = { onDateClick(date) },
                            onEventClick = onEventClick
                        )
                    } else {
                        // Empty cell or overflow day for adjacent months
                        val date = if (dayIndex < 1) {
                            val prevMonth = yearMonth.minusMonths(1)
                            prevMonth.atDay(prevMonth.lengthOfMonth() + dayIndex)
                        } else {
                            val nextMonth = yearMonth.plusMonths(1)
                            nextMonth.atDay(dayIndex - daysInMonth)
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(2.dp)
                                .clip(RoundedCornerShape(TileCornerRadius))
                                .border(
                                    0.5.dp,
                                    MaterialTheme.customColors.divider,
                                    RoundedCornerShape(TileCornerRadius)
                                )
                                .clickable { onDateClick(date) }
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                modifier = Modifier.padding(6.dp),
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
            .clip(RoundedCornerShape(TileCornerRadius))
            .background(Color.White)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) PurplePrimary else MaterialTheme.customColors.divider,
                shape = RoundedCornerShape(TileCornerRadius)
            )
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            // Day number highlight
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isToday) PurplePrimary else Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = dayNumber.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (isToday) PurplePrimary else MaterialTheme.customColors.textPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Event pills
            visibleEvents.forEach { event ->
                EventPill(
                    event = event,
                    onClick = onClick
                )
            }

            // Overflow indicator (+N)
            if (overflowCount > 0) {
                Text(
                    text = "+$overflowCount",
                    color = PurplePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 2.dp)
                )
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
            .height(18.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(event.color.copy(alpha = 0.9f))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}