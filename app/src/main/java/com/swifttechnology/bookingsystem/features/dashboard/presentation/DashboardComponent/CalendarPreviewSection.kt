package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius as AppCornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

 
//  Data models
 

private data class CalendarEvent(
    val title: String,
    val time: String,
    val room: String,
    val tag: String,
    val tagColor: Color,
    val accentColor: Color,
    val date: LocalDate
)

private data class WeekDay(
    val label: String,
    val meetingCount: Int,
    val isToday: Boolean = false
)

private data class CalendarDayCell(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val events: List<CalendarEvent>
)

 
//  Accent colours
 

private val PurpleAccent = Color(0xFF7C3AED)
private val OrangeAccent = Color(0xFFF97316)
private val BlueAccent   = Color(0xFF3B82F6)

 
//  Sample data  (today = Feb 8 2026 for demo)
 

@RequiresApi(Build.VERSION_CODES.O)
private val today = LocalDate.of(2026, 2, 8)

@RequiresApi(Build.VERSION_CODES.O)
private val sampleEvents = listOf(
    CalendarEvent("Q1 Financial Review",  "09:00 AM", "Executive Room 3A",  "executive", PurpleAccent, PurpleAccent, today),
    CalendarEvent("Client Presentation",  "11:00 AM", "Conference Room 2B", "client",    OrangeAccent, OrangeAccent, today),
    CalendarEvent("Sprint Planning",      "02:00 PM", "Meeting Room 1C",    "internal",  BlueAccent,   BlueAccent,   today),
    CalendarEvent("Design Review",        "10:00 AM", "Studio B",           "internal",  BlueAccent,   BlueAccent,   today.plusDays(2)),
    CalendarEvent("Vendor Call",          "03:00 PM", "Conf Room 4A",       "client",    OrangeAccent, OrangeAccent, today.plusDays(4)),
    CalendarEvent("Board Meeting",        "09:30 AM", "Boardroom",          "executive", PurpleAccent, PurpleAccent, today.plusDays(7)),
    CalendarEvent("Team Standup",         "09:00 AM", "Zoom",               "internal",  BlueAccent,   BlueAccent,   today.plusDays(9)),
    CalendarEvent("Budget Review",        "01:00 PM", "Finance Room",       "executive", PurpleAccent, PurpleAccent, today.plusDays(9)),
    CalendarEvent("Product Demo",         "11:00 AM", "Demo Suite",         "client",    OrangeAccent, OrangeAccent, today.plusDays(14)),
    CalendarEvent("Retrospective",        "04:00 PM", "Meeting Room 2",     "internal",  BlueAccent,   BlueAccent,   today.plusDays(17)),
)

private val weekDays = listOf(
    WeekDay("Sat, Feb 8",  3, isToday = true),
    WeekDay("Sun, Feb 9",  0),
    WeekDay("Mon, Feb 10", 1),
    WeekDay("Tue, Feb 11", 0),
    WeekDay("Wed, Feb 12", 1),
    WeekDay("Thu, Feb 13", 0),
    WeekDay("Fri, Feb 14", 0)
)

 
//  Public composable
 

@Composable
fun CalendarPreviewSection(
    onOpenFullCalendar: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("Day") }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        shape     = RoundedCornerShape(AppCornerRadius.lg),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(Elevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            //    Header row                               
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.onSurface,
                        modifier           = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text  = "Calendar Preview",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TabPills(
                    tabs          = listOf("Day", "Week", "Month"),
                    selectedTab   = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            Spacer(Modifier.height(Spacing.md))

            //    Tab content                              
            when (selectedTab) {
                "Day"   -> DayView()
                "Week"  -> WeekView()
                "Month" -> MonthView()
            }

            Spacer(Modifier.height(Spacing.md))

            //    Open Full Calendar button                 
            OutlinedButton(
                onClick  = onOpenFullCalendar,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(AppCornerRadius.md),
                border   = BorderStroke(1.dp, MaterialTheme.customColors.divider),
                colors   = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
            ) {
                Text(
                    text  = "Open Full Calendar",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

 
//  Tab pills  (Day / Week / Month)
 

@Composable
private fun TabPills(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AppCornerRadius.md))
            .background(MaterialTheme.customColors.dashboardBg)
            .padding(Spacing.xxs)
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AppCornerRadius.md))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = Spacing.ms, vertical = Spacing.xs),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = tab,
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color      = if (isSelected)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.customColors.textBody
                )
            }
        }
    }
}

 
//  Shared nav row
 

@Composable
private fun NavigationRow(
    label: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppCornerRadius.md))
            .background(MaterialTheme.customColors.dashboardBg)
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious, modifier = Modifier.size(Spacing.xl)) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = MaterialTheme.customColors.textBody)
        }
        Text(
            text       = label,
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onNext, modifier = Modifier.size(Spacing.xl)) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = MaterialTheme.customColors.textBody)
        }
    }
}

 
//  Day view
 

@Composable
private fun DayView() {
    var dayOffset by remember { mutableIntStateOf(0) }
    val currentDate = today.plusDays(dayOffset.toLong())

    val label = when (dayOffset) {
        0    -> "Today"
        1    -> "Tomorrow"
        -1   -> "Yesterday"
        else -> if (dayOffset > 0) "In $dayOffset days" else "${-dayOffset} days ago"
    }

    val dayEvents = sampleEvents.filter { it.date == currentDate }

    NavigationRow(
        label      = label,
        onPrevious = { dayOffset-- },
        onNext     = { dayOffset++ }
    )

    Spacer(Modifier.height(Spacing.ms))

    if (dayEvents.isEmpty()) {
        EmptyState("No events for this day")
    } else {
        dayEvents.forEach { event ->
            EventCard(event)
            Spacer(Modifier.height(Spacing.ms))
        }
    }
}

 
//  Week view
 

@Composable
private fun WeekView() {
    var weekOffset by remember { mutableIntStateOf(0) }

    val weekLabel = when (weekOffset) {
        0    -> "Week of Feb 8–14, 2026"
        else -> if (weekOffset > 0) "Week +$weekOffset" else "Week $weekOffset"
    }

    NavigationRow(
        label      = weekLabel,
        onPrevious = { weekOffset-- },
        onNext     = { weekOffset++ }
    )

    Spacer(Modifier.height(Spacing.ms))

    weekDays.forEach { day ->
        WeekDayRow(day)
        Spacer(Modifier.height(Spacing.sm))
    }

    Spacer(Modifier.height(Spacing.sm))

    Text(
        text       = "Today's Meetings",
        style      = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.onSurface
    )

    Spacer(Modifier.height(Spacing.sm))

    val todayEvents = sampleEvents.filter { it.date == today }
    if (todayEvents.isEmpty()) {
        EmptyState("No meetings today")
    } else {
        todayEvents.forEach { event ->
            WeekMeetingRow(event)
            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

 
//  Month view
 

@Composable
private fun MonthView() {
    var monthOffset by remember { mutableIntStateOf(0) }
    var selectedDate by remember { mutableStateOf(today) }

    val displayYearMonth = YearMonth.from(today).plusMonths(monthOffset.toLong())
    val monthLabel = displayYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

    NavigationRow(
        label      = monthLabel,
        onPrevious = { monthOffset-- },
        onNext     = { monthOffset++ }
    )

    Spacer(Modifier.height(Spacing.ms))

    //    Day-of-week header                       
    val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth()) {
        dayHeaders.forEach { day ->
            Text(
                text      = day,
                modifier  = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style     = MaterialTheme.typography.labelSmall,
                color     = MaterialTheme.customColors.textBody
            )
        }
    }

    Spacer(Modifier.height(Spacing.xs))

    //    Build day cells                          
    val cells = buildMonthCells(displayYearMonth)

    LazyVerticalGrid(
        columns             = GridCells.Fixed(7),
        modifier            = Modifier
            .fillMaxWidth()
            .height(230.dp),          // fixed height so the grid doesn't grow unbounded
        verticalArrangement   = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        userScrollEnabled     = false
    ) {
        items(cells) { cell ->
            MonthDayCell(
                cell         = cell,
                isSelected   = cell.date == selectedDate,
                onDaySelected = { selectedDate = it }
            )
        }
    }

    Spacer(Modifier.height(Spacing.md))

    HorizontalDivider(color = MaterialTheme.customColors.divider, thickness = 1.dp)

    Spacer(Modifier.height(Spacing.md))

    //    Day detail panel                          
    DayDetailPanel(selectedDate = selectedDate)
}

 
//  Build month cells helper
 

private fun buildMonthCells(yearMonth: YearMonth): List<CalendarDayCell> {
    val firstOfMonth = yearMonth.atDay(1)
    val firstDow     = firstOfMonth.dayOfWeek          // e.g. SUNDAY
    // How many days to show from the previous month
    val prefixDays   = (firstDow.value % 7)            // Sun=0, Mon=1, …, Sat=6

    val cells = mutableListOf<CalendarDayCell>()

    // Previous month tail
    val prevMonth = yearMonth.minusMonths(1)
    val prevLen   = prevMonth.lengthOfMonth()
    for (i in prefixDays downTo 1) {
        val date = prevMonth.atDay(prevLen - i + 1)
        cells += CalendarDayCell(date, false, false, emptyList())
    }

    // Current month
    for (d in 1..yearMonth.lengthOfMonth()) {
        val date     = yearMonth.atDay(d)
        val isToday  = date == today
        val dayEvs   = sampleEvents.filter { it.date == date }
        cells += CalendarDayCell(date, true, isToday, dayEvs)
    }

    // Next month head  (fill out to complete the last row)
    val remainder = (7 - (cells.size % 7)) % 7
    val nextMonth = yearMonth.plusMonths(1)
    for (d in 1..remainder) {
        cells += CalendarDayCell(nextMonth.atDay(d), false, false, emptyList())
    }

    return cells
}

 
//  Month day cell
 

@Composable
private fun MonthDayCell(
    cell: CalendarDayCell,
    isSelected: Boolean,
    onDaySelected: (LocalDate) -> Unit
) {
    val bgColor = when {
        cell.isToday   -> MaterialTheme.colorScheme.primary
        isSelected     -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else           -> Color.Transparent
    }

    val textColor = when {
        cell.isToday        -> MaterialTheme.colorScheme.onPrimary
        !cell.isCurrentMonth -> MaterialTheme.customColors.textBody.copy(alpha = 0.4f)
        else                -> MaterialTheme.colorScheme.onSurface
    }

    val borderStroke = when {
        isSelected && !cell.isToday -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        else                        -> null
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(AppCornerRadius.sm))
            .background(bgColor)
            .then(
                if (borderStroke != null)
                    Modifier.background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        RoundedCornerShape(AppCornerRadius.sm)
                    )
                else Modifier
            )
            .clickable(enabled = cell.isCurrentMonth) { onDaySelected(cell.date) }
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text      = cell.date.dayOfMonth.toString(),
            style     = MaterialTheme.typography.labelSmall,
            color     = textColor,
            textAlign = TextAlign.Center
        )

        // Event dots (max 3)
        if (cell.events.isNotEmpty() && cell.isCurrentMonth) {
            Spacer(Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                cell.events.take(3).forEach { ev ->
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (cell.isToday) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                else ev.accentColor
                            )
                    )
                }
            }
        }
    }
}

 
//  Day detail panel  (used in Month view)
 

@Composable
private fun DayDetailPanel(selectedDate: LocalDate) {
    val dayEvents = sampleEvents.filter { it.date == selectedDate }

    val dateLabel = selectedDate.format(
        DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
    )

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = dateLabel,
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface
        )
        if (dayEvents.isNotEmpty()) {
            Text(
                text  = "${dayEvents.size} event${if (dayEvents.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.customColors.textBody
            )
        }
    }

    Spacer(Modifier.height(Spacing.sm))

    if (dayEvents.isEmpty()) {
        EmptyState("No events scheduled")
    } else {
        dayEvents.forEach { event ->
            EventCard(event)
            Spacer(Modifier.height(Spacing.ms))
        }
    }
}

 
//  Event card  (Day view & Month detail)
 

@Composable
private fun EventCard(event: CalendarEvent) {
    val accent = event.accentColor

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(AppCornerRadius.lg),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(Elevation.sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color        = accent,
                        topLeft      = Offset(0f, 8.dp.toPx()),
                        size         = Size(4.dp.toPx(), size.height - 16.dp.toPx()),
                        cornerRadius = CornerRadius(2.dp.toPx())
                    )
                }
                .padding(start = Spacing.md, end = Spacing.md, top = Spacing.md, bottom = Spacing.md),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = event.title,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text  = event.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.customColors.textBody
                )
                Text(
                    text  = event.room,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.customColors.textBody
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(AppCornerRadius.sm))
                    .background(event.tagColor.copy(alpha = 0.1f))
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
            ) {
                Text(
                    text  = event.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = event.tagColor
                )
            }
        }
    }
}

 
//  Week day row
 

@Composable
private fun WeekDayRow(day: WeekDay) {
    val bgColor = if (day.isToday)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    else
        MaterialTheme.colorScheme.surface

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(AppCornerRadius.md),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(Elevation.none),
        border    = if (day.isToday)
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        else
            BorderStroke(1.dp, MaterialTheme.customColors.divider)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.ms),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text       = day.label,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "${day.meetingCount} meeting${if (day.meetingCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.customColors.textBody
                )
            }

            if (day.meetingCount > 0) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(AppCornerRadius.sm))
                        .background(MaterialTheme.customColors.dashboardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "${day.meetingCount}",
                        style      = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

 
//  Week meeting row  (today's meetings in week view)
 

@Composable
private fun WeekMeetingRow(event: CalendarEvent) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(AppCornerRadius.md),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(Elevation.none),
        border    = BorderStroke(1.dp, MaterialTheme.customColors.divider)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.ms),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(event.accentColor)
            )
            Spacer(Modifier.width(Spacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = event.title,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = event.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.customColors.textBody
                )
            }
        }
    }
}

 
//  Empty state
 

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(AppCornerRadius.md))
            .background(MaterialTheme.customColors.dashboardBg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.customColors.textBody
        )
    }
}