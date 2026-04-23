package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.weekview

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

private val PurplePrimary = Color(0xFF6C3EE8)
private val PurpleBg = Color(0xFFFCF9FF)
private val BorderColor = Color(0xFFD6D5D4)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekView(
    selectedDate: LocalDate,
    events: List<MeetingEvent>,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (MeetingEvent) -> Unit
) {
    val startOfWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val leftDate = selectedDate
    val rightDate = selectedDate.plusDays(1)

    Column(modifier = Modifier.fillMaxSize()) {
        //    Horizontal-scrollable week day strip                             
        WeekDayStrip(
            startOfWeek = startOfWeek,
            selectedDate = leftDate,
            onDayClick = onDateClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        //    Two-column visible day label row                                  
        VisibleDayLabels(left = leftDate, right = rightDate)
        
        Spacer(modifier = Modifier.height(8.dp))

        //    Calendar grid                                                   
        WeekCalendarGrid(
            leftDate = leftDate,
            rightDate = rightDate,
            events = events,
            onEventClick = onEventClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekDayStrip(
    startOfWeek: LocalDate,
    selectedDate: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        for (i in 0..6) {
            val date = startOfWeek.plusDays(i.toLong())
            val isLeftSelected = date == selectedDate
            val isRightSelected = date == selectedDate.plusDays(1)
            
            DayCell(
                date = date,
                isLeftSelected = isLeftSelected,
                isRightSelected = isRightSelected,
                onClick = { onDayClick(date) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayCell(
    date: LocalDate, 
    isLeftSelected: Boolean, 
    isRightSelected: Boolean, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = date == LocalDate.now()
    val dayOfWeekLabel = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = dayOfWeekLabel.take(3),
            fontSize = 10.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) PurplePrimary else MaterialTheme.customColors.textSecondary,
            textAlign = TextAlign.Center
        )

        val pillBgColor = Color(0xFFEBEBEB)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isLeftSelected || isRightSelected) {
                Row(modifier = Modifier.fillMaxWidth().height(30.dp)) {
                    // Left half (connects to left side if this is the right day)
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(
                        if (isRightSelected && date.dayOfWeek != DayOfWeek.SUNDAY) pillBgColor else Color.Transparent
                    ))
                    // Right half (connects to right side if this is the left day)
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(
                        if (isLeftSelected && date.dayOfWeek != DayOfWeek.SATURDAY) pillBgColor else Color.Transparent
                    ))
                }
                // Center circle to round the grey background at the day's position
                Box(modifier = Modifier.size(30.dp).background(pillBgColor, CircleShape))
            }

            val circleColor = when {
                isLeftSelected && isToday -> PurplePrimary
                isLeftSelected -> Color.Black
                else -> Color.Transparent
            }
            
            val textColor = when {
                isLeftSelected -> Color.White
                isToday -> PurplePrimary
                isRightSelected -> Color.Black
                else -> MaterialTheme.customColors.textPrimary
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(circleColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 13.sp,
                    fontWeight = if (isLeftSelected || isRightSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun VisibleDayLabels(left: LocalDate, right: LocalDate) {
    val leftIsToday = left == LocalDate.now()
    val rightIsToday = right == LocalDate.now()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.width(50.dp)) // Time column spacer

        Text(
            text = "${left.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).capitalize()} - ${left.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${left.dayOfMonth.toString().padStart(2, '0')}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = if (leftIsToday) PurplePrimary else MaterialTheme.customColors.textPrimary
        )
        Text(
            text = "${right.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).capitalize()} - ${right.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${right.dayOfMonth.toString().padStart(2, '0')}",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = if (rightIsToday) PurplePrimary else MaterialTheme.customColors.textPrimary
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekCalendarGrid(
    leftDate: LocalDate,
    rightDate: LocalDate,
    events: List<MeetingEvent>,
    onEventClick: (MeetingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeSlots = (0..23).map {
        if (it == 0) "12 AM"
        else if (it < 12) "$it AM"
        else if (it == 12) "12 PM"
        else "${it - 12} PM"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            //    Time labels column                                           
            Column(
                modifier = Modifier
                    .width(50.dp)
                    .background(Color.White)
            ) {
                timeSlots.forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .border(width = 0.5.dp, color = BorderColor.copy(alpha = 0.5f))
                            .background(Color.White),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = MaterialTheme.customColors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            //    Left Day column                                                  
            DayColumn(
                date = leftDate,
                events = events.filter { it.date == leftDate },
                onEventClick = onEventClick,
                modifier = Modifier.weight(1f)
            )

            //    Right Day column                                                  
            DayColumn(
                date = rightDate,
                events = events.filter { it.date == rightDate },
                onEventClick = onEventClick,
                modifier = Modifier.weight(1f),
                isLast = true
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayColumn(
    date: LocalDate,
    events: List<MeetingEvent>,
    onEventClick: (MeetingEvent) -> Unit,
    modifier: Modifier = Modifier,
    isLast: Boolean = false
) {
    Box(
        modifier = modifier
            .background(PurpleBg)
            .border(width = 0.5.dp, color = BorderColor.copy(alpha = 0.5f))
    ) {
        Column {
            for (hour in 0..23) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .border(width = 0.5.dp, color = BorderColor.copy(alpha = 0.5f))
                        .background(PurpleBg)
                )
            }
        }

        events.forEach { event ->
            val startMinutes = event.startTime.hour * 60 + event.startTime.minute
            val endMinutes = event.endTime.hour * 60 + event.endTime.minute
            val duration = endMinutes - startMinutes

            val eventColor = event.backendColor ?: event.color
            
            // 1 minute = 1 dp (since 1 hour = 60 dp)
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .fillMaxWidth()
                    .height((duration).dp)
                    .offset(y = (startMinutes).dp)
                    .background(eventColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .border(1.dp, eventColor, RoundedCornerShape(4.dp))
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

// Extension for capitalizing string
private fun String.capitalize(): String =
    if (isEmpty()) this
    else this[0].uppercaseChar() + substring(1).lowercase(Locale.getDefault())
