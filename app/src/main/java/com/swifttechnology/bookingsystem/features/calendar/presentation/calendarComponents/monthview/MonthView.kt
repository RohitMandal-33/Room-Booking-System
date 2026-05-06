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
import com.swifttechnology.bookingsystem.core.designsystem.CalendarTodayPurple
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import java.time.LocalDate
import java.time.YearMonth

private val MaxVisibleEvents = 3
private val PillHeight      = 14.dp
private val TileHeight      = 100.dp
private val TileCorner      = 8.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthView(
    yearMonth: YearMonth,
    events: List<MeetingEvent>,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
    onEventClick: (MeetingEvent) -> Unit
) {
    val today        = LocalDate.now()
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek  = firstDayOfMonth.dayOfWeek.value % 7   // Sunday = 0
    val daysInMonth     = yearMonth.lengthOfMonth()

    // Tile surface — uses the --bg-surface token via colorScheme.surface
    val tileBg     = MaterialTheme.colorScheme.surface
    val tileStroke = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        for (row in 0 until 6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TileHeight)
            ) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - firstDayOfWeek + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date      = yearMonth.atDay(dayIndex)
                        val isSelected = date == selectedDate
                        val isToday   = date == today
                        val dayEvents = events.filter { it.date == date }

                        MonthDayTile(
                            dayNumber   = dayIndex,
                            isSelected  = isSelected,
                            isToday     = isToday,
                            events      = dayEvents,
                            tileBg      = tileBg,
                            tileStroke  = tileStroke,
                            modifier    = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(2.dp),
                            onClick      = { onDateClick(date) },
                            onEventClick = onEventClick
                        )
                    } else {
                        // Adjacent-month overflow — same surface tile, muted text
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
                                .clip(RoundedCornerShape(TileCorner))
                                .background(tileBg)
                                .border(0.5.dp, tileStroke, RoundedCornerShape(TileCorner))
                                .clickable { onDateClick(date) }
                        ) {
                            Text(
                                text     = date.dayOfMonth.toString(),
                                modifier = Modifier.padding(6.dp),
                                style    = MaterialTheme.typography.bodySmall,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
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
    tileBg: Color,
    tileStroke: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEventClick: (MeetingEvent) -> Unit
) {
    val visibleEvents = events.take(MaxVisibleEvents)
    val overflowCount = events.size - visibleEvents.size

    // Tile card: surface background + hairline border.
    // Selected tile gets a CalendarTodayPurple accent border; today uses its circle instead.
    val borderColor = when {
        isSelected -> CalendarTodayPurple
        else       -> tileStroke
    }
    val borderWidth = if (isSelected) 1.5.dp else 0.5.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(TileCorner))
            .background(tileBg)
            .border(borderWidth, borderColor, RoundedCornerShape(TileCorner))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            // Day number — always solid purple circle for today; selected-only circle otherwise
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                if (isToday) {
                    // Single solid purple circle for today's date
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(CalendarTodayPurple),
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
                } else if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Text(
                        text = dayNumber.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Event pills
            visibleEvents.forEach { event ->
                EventPill(
                    event = event
                )
            }

            // Overflow indicator (+N)
            if (overflowCount > 0) {
                Text(
                    text = "+$overflowCount more",
                    color = CalendarTodayPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventPill(
    event: MeetingEvent
) {
    val pillColor = event.backendColor ?: event.color
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PillHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(pillColor.copy(alpha = 0.35f))
            .border(1.dp, pillColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}