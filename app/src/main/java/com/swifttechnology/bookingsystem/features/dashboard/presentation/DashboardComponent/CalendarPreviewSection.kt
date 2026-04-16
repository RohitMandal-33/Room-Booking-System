package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius as AppCornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
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

private data class CalendarDayCell(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val events: List<CalendarEvent>
)

/** Preset quick-pick ranges shown as pills above the calendar. */
enum class DateRangePreset { TODAY, THIS_WEEK, THIS_MONTH, LAST_30_DAYS, CUSTOM }

private data class DateRange(
    val start: LocalDate,
    val end: LocalDate
)

  
//  Accent colours  (aligned to brand palette)
  

private val PurpleAccent = Color(0xFF7C3AED)
private val OrangeAccent = Color(0xFFF97316)
private val BlueAccent   = Color(0xFF3B82F6)

  
//  Preset helpers
  

@RequiresApi(Build.VERSION_CODES.O)
private fun DateRangePreset.toRange(today: LocalDate): DateRange? = when (this) {
    DateRangePreset.TODAY        -> DateRange(today, today)
    DateRangePreset.THIS_WEEK    -> {
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        DateRange(startOfWeek, startOfWeek.plusDays(6))
    }
    DateRangePreset.THIS_MONTH   -> DateRange(
        today.withDayOfMonth(1),
        today.withDayOfMonth(today.lengthOfMonth())
    )
    DateRangePreset.LAST_30_DAYS -> DateRange(today.minusDays(29), today)
    DateRangePreset.CUSTOM       -> null   // user picks manually on the grid
}

private val DateRangePreset.label: String
    get() = when (this) {
        DateRangePreset.TODAY        -> "Today"
        DateRangePreset.THIS_WEEK    -> "This week"
        DateRangePreset.THIS_MONTH   -> "This month"
        DateRangePreset.LAST_30_DAYS -> "Last 30 days"
        DateRangePreset.CUSTOM       -> "Custom"
    }

  
//  Public composable
  

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarPreviewSection(
    events: List<com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO> = emptyList(),
    onOpenFullCalendar: () -> Unit = {}
) {
    // Stable today reference inside composition
    val today = remember { LocalDate.now() }

    //      Range state                                                                                                                     
    var activePreset       by remember { mutableStateOf(DateRangePreset.TODAY) }
    var rangeStart         by remember { mutableStateOf<LocalDate?>(today) }
    var rangeEnd           by remember { mutableStateOf<LocalDate?>(today) }
    var customPickingStart by remember { mutableStateOf(true) }

    // Which month the calendar grid is showing
    var monthOffset    by remember { mutableIntStateOf(0) }
    val displayYearMonth = YearMonth.from(today).plusMonths(monthOffset.toLong())

    //      Map incoming events                                                                                                     
    val calendarEvents = remember(events) { events.mapNotNull { it.toCalendarEvent() } }

    //      Derive events that fall within the current range                                           
    val rangeEvents = remember(rangeStart, rangeEnd, calendarEvents) {
        val s = rangeStart ?: return@remember emptyList()
        val e = rangeEnd   ?: s
        calendarEvents
            .filter { it.date in s..e }
            .sortedBy { it.date }
    }

    //      Helper: apply a preset                                                                                               
    fun applyPreset(preset: DateRangePreset) {
        activePreset = preset
        val range = preset.toRange(today)
        if (range != null) {
            rangeStart  = range.start
            rangeEnd    = range.end
            monthOffset = YearMonth.from(range.start)
                .let { it.year * 12 + it.monthValue } -
                    YearMonth.from(today).let { it.year * 12 + it.monthValue }
        } else {
            rangeStart         = null
            rangeEnd           = null
            customPickingStart = true
        }
    }

    //      Handle a tap on a grid day                                                                                       
    fun handleDayTap(tapped: LocalDate) {
        if (activePreset != DateRangePreset.CUSTOM) {
            rangeStart         = tapped
            rangeEnd           = tapped
            activePreset       = DateRangePreset.CUSTOM
            customPickingStart = false
            return
        }
        if (customPickingStart) {
            rangeStart         = tapped
            rangeEnd           = null
            customPickingStart = false
        } else {
            if (tapped < (rangeStart ?: tapped)) {
                rangeEnd   = rangeStart
                rangeStart = tapped
            } else {
                rangeEnd = tapped
            }
            customPickingStart = true
        }
    }

    //      UI                                                                                                                                       
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        shape     = RoundedCornerShape(AppCornerRadius.xl),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(Elevation.sm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {

            //      Header                                                                                                               
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector        = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text       = "Calendar Preview",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    modifier   = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(Spacing.md))

            //      Preset pills                                                                                                   
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement   = Arrangement.spacedBy(Spacing.sm),
                modifier              = Modifier.fillMaxWidth()
            ) {
                DateRangePreset.entries.forEach { preset ->
                    RangePill(
                        label    = preset.label,
                        isActive = preset == activePreset,
                        onClick  = { applyPreset(preset) }
                    )
                }
            }

            //      Inline range label                                                                                         
            val rangeText = buildRangeLabel(rangeStart, rangeEnd, activePreset)
            if (rangeText.isNotEmpty()) {
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text  = rangeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(Spacing.md))

            //      Month calendar                                                                                                 
            MonthView(
                today          = today,
                events         = calendarEvents,
                displayYearMonth = displayYearMonth,
                rangeStart     = rangeStart,
                rangeEnd       = rangeEnd,
                onDayTap       = { handleDayTap(it) },
                onPrevMonth    = { monthOffset-- },
                onNextMonth    = { monthOffset++ }
            )

            Spacer(Modifier.height(Spacing.sm))
            HorizontalDivider(
                color     = MaterialTheme.customColors.divider,
                thickness = 1.dp
            )
            Spacer(Modifier.height(Spacing.sm))

            //      Range event list                                                                                             
            RangeEventPanel(
                rangeStart = rangeStart,
                rangeEnd   = rangeEnd,
                events     = rangeEvents
            )

            Spacer(Modifier.height(Spacing.md))

            //      Open full calendar CTA                                                                                 
            FilledTonalButton(
                onClick  = onOpenFullCalendar,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(AppCornerRadius.lg),
                colors   = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.customColors.primaryLight,
                    contentColor   = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector        = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text       = "Open Full Calendar",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

  
//  Preset pill chip
  

@Composable
private fun RangePill(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bg     = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val fg     = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.customColors.textBody
    val border = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.customColors.divider

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = Spacing.ms, vertical = Spacing.xs + 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelMedium,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color      = fg
        )
    }
}

  
//  Range label text helper
  

@RequiresApi(Build.VERSION_CODES.O)
private fun buildRangeLabel(
    start: LocalDate?,
    end: LocalDate?,
    preset: DateRangePreset
): String {
    val fmt = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    return when {
        preset == DateRangePreset.CUSTOM && start == null -> "Tap a start date on the calendar"
        preset == DateRangePreset.CUSTOM && end == null   -> "From ${start!!.format(fmt)} — tap an end date"
        start == null                                     -> ""
        end == null || start == end                       -> start.format(fmt)
        else -> "${start.format(fmt)}  –  ${end.format(fmt)}"
    }
}

  
//  Navigation row
  

@Composable
private fun NavigationRow(
    label: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppCornerRadius.lg))
            .background(MaterialTheme.customColors.dashboardBg)
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        IconButton(
            onClick  = onPrevious,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.ChevronLeft,
                contentDescription = "Previous month",
                tint               = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text       = label,
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface
        )
        IconButton(
            onClick  = onNext,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = "Next month",
                tint               = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

  
//  Month grid view
  

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthView(
    today: LocalDate,
    events: List<CalendarEvent>,
    displayYearMonth: YearMonth,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    onDayTap: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthLabel = displayYearMonth.format(
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    )

    NavigationRow(label = monthLabel, onPrevious = onPrevMonth, onNext = onNextMonth)

    Spacer(Modifier.height(Spacing.sm))

    // Day-of-week header
    val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(modifier = Modifier.fillMaxWidth()) {
        dayHeaders.forEach { day ->
            Text(
                text      = day,
                modifier  = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style     = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color     = MaterialTheme.customColors.textBody
            )
        }
    }

    Spacer(Modifier.height(Spacing.xs))

    val cells = buildMonthCells(today, displayYearMonth, events)

    LazyVerticalGrid(
        columns               = GridCells.Fixed(7),
        modifier              = Modifier
            .fillMaxWidth()
            .height(240.dp),
        verticalArrangement   = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        userScrollEnabled     = false
    ) {
        items(cells) { cell ->
            MonthDayCell(
                cell       = cell,
                rangeStart = rangeStart,
                rangeEnd   = rangeEnd,
                onDayTap   = onDayTap
            )
        }
    }
}

  
//  Month day cell  (range-aware)
  

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthDayCell(
    cell: CalendarDayCell,
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    onDayTap: (LocalDate) -> Unit
) {
    val effectiveEnd = rangeEnd ?: rangeStart

    val isStart   = rangeStart != null && cell.date == rangeStart
    val isEnd     = effectiveEnd != null && cell.date == effectiveEnd
    val isInRange = rangeStart != null && effectiveEnd != null &&
            cell.date > rangeStart && cell.date < effectiveEnd

    val primaryLight = MaterialTheme.customColors.primaryLight

    val bgColor = when {
        isStart || isEnd -> MaterialTheme.colorScheme.primary
        cell.isToday     -> primaryLight
        isInRange        -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else             -> Color.Transparent
    }

    val textColor = when {
        isStart || isEnd     -> MaterialTheme.colorScheme.onPrimary
        !cell.isCurrentMonth -> MaterialTheme.customColors.textBody.copy(alpha = 0.3f)
        cell.isToday         -> MaterialTheme.colorScheme.primary
        else                 -> MaterialTheme.colorScheme.onSurface
    }

    val shape = when {
        isStart || isEnd -> CircleShape
        isInRange        -> RoundedCornerShape(0.dp)
        else             -> RoundedCornerShape(AppCornerRadius.sm)
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(bgColor)
            .clickable(enabled = cell.isCurrentMonth) { onDayTap(cell.date) }
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text       = cell.date.dayOfMonth.toString(),
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = if (isStart || isEnd || cell.isToday) FontWeight.Bold else FontWeight.Normal,
            color      = textColor,
            textAlign  = TextAlign.Center
        )

        // Event dots (up to 3)
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
                                if (isStart || isEnd) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                else ev.accentColor
                            )
                    )
                }
            }
        }
    }
}

  
//  Range event panel
  

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RangeEventPanel(
    rangeStart: LocalDate?,
    rangeEnd: LocalDate?,
    events: List<CalendarEvent>
) {
    if (rangeStart == null) {
        EmptyState("Select a date or range to see events")
        return
    }

    val effectiveEnd = rangeEnd ?: rangeStart
    val fmt          = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
    val headerLabel  = if (rangeStart == effectiveEnd)
        rangeStart.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()))
    else
        "${rangeStart.format(fmt)} – ${effectiveEnd.format(fmt)}"

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = headerLabel,
            style      = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface,
            modifier   = Modifier.weight(1f),
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
        if (events.isNotEmpty()) {
            Spacer(Modifier.width(Spacing.sm))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.customColors.primaryLight)
                    .padding(horizontal = Spacing.sm, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text      = "${events.size} event${if (events.size != 1) "s" else ""}",
                    style     = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color     = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    Spacer(Modifier.height(Spacing.sm))

    if (events.isEmpty()) {
        EmptyState("No events in this range")
    } else {
        events.forEach { event ->
            EventRow(event, showDate = rangeStart != effectiveEnd)
            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

  
//  Event row  (flat — avoids nested Card visual noise)
  

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventRow(event: CalendarEvent, showDate: Boolean = false) {
    val accent    = event.accentColor
    val dateBadge = if (showDate)
        event.date.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault()))
    else null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppCornerRadius.lg))
            .background(MaterialTheme.customColors.dashboardBg)
            .drawBehind {
                // Left accent bar — full height minus corner inset
                val barWidth    = 4.dp.toPx()
                val cornerInset = AppCornerRadius.lg.toPx()
                drawRoundRect(
                    color        = accent,
                    topLeft      = Offset(0f, cornerInset),
                    size         = Size(barWidth, size.height - cornerInset * 2),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
            .padding(
                start  = Spacing.md,
                end    = Spacing.sm,
                top    = Spacing.sm,
                bottom = Spacing.sm
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = event.title,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            if (dateBadge != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = dateBadge,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text  = buildEventSubtitle(event),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.customColors.textBody,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(Spacing.sm))

        // Tag badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(AppCornerRadius.md))
                .background(accent.copy(alpha = 0.12f))
                .padding(horizontal = Spacing.sm, vertical = Spacing.xxs + 1.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = event.tag,
                style      = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color      = accent
            )
        }
    }
}

/** Combines time + room into a single subtitle line. */
private fun buildEventSubtitle(event: CalendarEvent): String {
    val parts = listOfNotNull(
        event.time.takeIf { it.isNotEmpty() },
        event.room.takeIf { it.isNotEmpty() && it != "No Room" }
    )
    return parts.joinToString("  ·  ")
}

  
//  Build month cells helper
  

@RequiresApi(Build.VERSION_CODES.O)
private fun buildMonthCells(
    today: LocalDate,
    yearMonth: YearMonth,
    events: List<CalendarEvent>
): List<CalendarDayCell> {
    val firstOfMonth = yearMonth.atDay(1)
    val prefixDays   = firstOfMonth.dayOfWeek.value % 7   // Sun=0 … Sat=6

    val cells = mutableListOf<CalendarDayCell>()

    val prevMonth = yearMonth.minusMonths(1)
    val prevLen   = prevMonth.lengthOfMonth()
    for (i in prefixDays downTo 1) {
        val date = prevMonth.atDay(prevLen - i + 1)
        cells += CalendarDayCell(date, false, false, emptyList())
    }

    for (d in 1..yearMonth.lengthOfMonth()) {
        val date   = yearMonth.atDay(d)
        val dayEvs = events.filter { it.date == date }
        cells += CalendarDayCell(date, true, date == today, dayEvs)
    }

    val remainder = (7 - (cells.size % 7)) % 7
    val nextMonth = yearMonth.plusMonths(1)
    for (d in 1..remainder) {
        cells += CalendarDayCell(nextMonth.atDay(d), false, false, emptyList())
    }

    return cells
}

  
//  Mapper
  

@RequiresApi(Build.VERSION_CODES.O)
private fun com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO.toCalendarEvent(): CalendarEvent? {
    val dateStr = this.date ?: return null
    return try {
        val ld        = LocalDate.parse(dateStr)
        val timeLabel = when (val st = this.startTime) {
            is String -> st
            is com.swifttechnology.bookingsystem.features.booking.data.dtos.LocalTimeDTO ->
                "${st.hour}:${st.minute.toString().padStart(2, '0')}"
            else -> ""
        }
        val color = when (this.meetingType?.uppercase()) {
            "EXECUTIVE" -> PurpleAccent
            "CLIENT"    -> OrangeAccent
            else        -> BlueAccent
        }
        CalendarEvent(
            title       = this.meetingTitle ?: "Unnamed Meeting",
            time        = timeLabel,
            room        = this.room?.roomName ?: "No Room",
            tag         = this.meetingType  ?: "INTERNAL",
            tagColor    = color,
            accentColor = color,
            date        = ld
        )
    } catch (e: Exception) {
        null
    }
}

  
//  Empty state
  

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(AppCornerRadius.lg))
            .background(MaterialTheme.customColors.dashboardBg)
            .padding(vertical = Spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.customColors.textBody,
            textAlign = TextAlign.Center
        )
    }
}