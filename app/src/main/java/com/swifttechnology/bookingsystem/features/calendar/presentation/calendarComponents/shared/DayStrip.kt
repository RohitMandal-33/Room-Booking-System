package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

private val PurplePrimary = Color(0xFF6C3EE8)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayStrip(
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    onPreviousWeek: (() -> Unit)? = null,
    onNextWeek: (() -> Unit)? = null
) {
    // Start week on Sunday
    val weekStart = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val weekDays = (0..6).map { weekStart.plusDays(it.toLong()) }
    val monthYearLabel = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Only show header (Month/Year + arrows) if navigation callbacks are provided.
        // This removes the non-functional/duplicate header in screens like CalendarScreen.
        if (onPreviousWeek != null || onNextWeek != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onPreviousWeek?.invoke() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous week",
                        tint = Color(0xFF333333)
                    )
                }
                Text(
                    text = monthYearLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
                IconButton(
                    onClick = { onNextWeek?.invoke() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next week",
                        tint = Color(0xFF333333)
                    )
                }
            }
        }

        // Day-of-week + date number row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                val isSelected = day == selectedDate
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDateClick(day) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            .take(3),
                        fontSize = 10.sp,
                        color = Color(0xFF888888),
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PurplePrimary else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.dayOfMonth.toString(),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF111111)
                        )
                    }
                    // Dot indicator for today
                    if (day == LocalDate.now()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else PurplePrimary)
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = Color(0xFFEEEEEE))
    }
}
