package com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

private val Purple50  = Color(0xFFEEEDFE)
private val Purple200 = Color(0xFFAFA9EC)
private val Purple400 = Color(0xFF7F77DD)
private val Purple600 = Color(0xFF534AB7)
private val Purple900 = Color(0xFF2D2563)
private val TextPrimary   = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF888888)
private val BorderLight   = Color(0xFFE0E0E0)
private val SurfaceGray   = Color(0xFFF5F5F5)

private val shortMonths = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
private val fullMonths  = listOf("January","February","March","April","May","June",
    "July","August","September","October","November","December")
private val weekDays    = listOf("Su","Mo","Tu","We","Th","Fr","Sa")

data class CalDate(val year: Int, val month: Int, val day: Int) {
    fun toCalendar(): Calendar = Calendar.getInstance().apply {
        set(year, month, day, 0, 0, 0); set(Calendar.MILLISECOND, 0)
    }
    fun toMillis() = toCalendar().timeInMillis
    fun isBefore(other: CalDate) = toMillis() < other.toMillis()
    fun isAfter(other: CalDate)  = toMillis() > other.toMillis()
    fun isSameDay(other: CalDate?) = other != null && year == other.year && month == other.month && day == other.day
    fun formatted() = "${shortMonths[month]} $day, $year"
}

private fun todayCalDate(): CalDate {
    val c = Calendar.getInstance()
    return CalDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
}

private fun daysInMonth(year: Int, month: Int) =
    Calendar.getInstance().apply { set(year, month, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)

private fun firstDayOfWeek(year: Int, month: Int) =
    Calendar.getInstance().apply { set(year, month, 1) }.get(Calendar.DAY_OF_WEEK) - 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSheet(
    initialStartMillis: Long? = null,
    initialEndMillis: Long? = null,
    onDismiss: () -> Unit,
    onApply: (Long, Long) -> Unit
) {
    val today = remember { todayCalDate() }
    var startDate by remember { mutableStateOf<CalDate?>(null) }
    var endDate   by remember { mutableStateOf<CalDate?>(null) }
    var picking   by remember { mutableStateOf("start") } // "start" | "end"
    var viewYear  by remember { mutableStateOf(today.year) }
    var viewMonth by remember { mutableStateOf(today.month) }
    var activePreset by remember { mutableStateOf("week") }

    fun applyPreset(key: String) {
        activePreset = key
        val c = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        when (key) {
            "today" -> {
                startDate = today; endDate = today
            }
            "week" -> {
                val dow = c.get(Calendar.DAY_OF_WEEK) - 1
                c.add(Calendar.DAY_OF_YEAR, -dow)
                startDate = CalDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
                c.add(Calendar.DAY_OF_YEAR, 6)
                endDate = CalDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            }
            "month" -> {
                startDate = CalDate(today.year, today.month, 1)
                endDate   = CalDate(today.year, today.month, daysInMonth(today.year, today.month))
            }
            "30days" -> {
                endDate = today
                c.add(Calendar.DAY_OF_YEAR, -30)
                startDate = CalDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            }
            "custom" -> { startDate = null; endDate = null; picking = "start" }
        }
        startDate?.let { viewYear = it.year; viewMonth = it.month }
        if (key != "custom") picking = "start"
    }

    // Init logic
    LaunchedEffect(initialStartMillis, initialEndMillis) {
        val c = Calendar.getInstance()
        if (initialStartMillis != null) {
            c.timeInMillis = initialStartMillis
            startDate = CalDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            viewYear = startDate!!.year
            viewMonth = startDate!!.month
        }
        if (initialEndMillis != null) {
            c.timeInMillis = initialEndMillis
            endDate = CalDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        }
        if (initialStartMillis == null && initialEndMillis == null) {
            applyPreset("week")
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
        modifier = Modifier.wrapContentHeight()
    ) {
        Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {

            // ── Handle
            Box(Modifier.fillMaxWidth().padding(top = 10.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(width = 36.dp, height = 4.dp).background(BorderLight, RoundedCornerShape(2.dp)))
            }

            // ── Header
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select date range", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary))
                Text("Cancel", style = TextStyle(fontSize = 14.sp, color = TextSecondary), modifier = Modifier.clickable { onDismiss() })
            }

            // ── Start / End boxes
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                // Start box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, if (picking == "start") Purple600 else Color.Transparent, RoundedCornerShape(12.dp))
                        .background(Purple50)
                        .clickable { picking = "start" }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text("START DATE", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Purple400, letterSpacing = 0.5.sp))
                    Spacer(Modifier.height(3.dp))
                    Text(
                        startDate?.formatted() ?: "Select",
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                            color = if (startDate != null) Purple900 else Purple200)
                    )
                }

                Text("→", modifier = Modifier.padding(horizontal = 10.dp), color = Purple200, fontSize = 18.sp)

                // End box
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, if (picking == "end") Purple600 else Color.Transparent, RoundedCornerShape(12.dp))
                        .background(Purple50)
                        .clickable { picking = "end" }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text("END DATE", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Purple400, letterSpacing = 0.5.sp))
                    Spacer(Modifier.height(3.dp))
                    Text(
                        endDate?.formatted() ?: "Select",
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                            color = if (endDate != null) Purple900 else Purple200)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Quick preset pills
            val presets = listOf("today" to "Today", "week" to "This week", "month" to "This month", "30days" to "Last 30 days", "custom" to "Custom")
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { (key, label) ->
                    val isOn = activePreset == key
                    Text(
                        label,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium,
                            color = if (isOn) Purple600 else TextSecondary),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(0.5.dp, if (isOn) Purple200 else BorderLight, RoundedCornerShape(20.dp))
                            .background(if (isOn) Purple50 else Color.White)
                            .clickable { applyPreset(key) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(8.dp))

            // ── Month navigation
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (viewMonth == 0) { viewMonth = 11; viewYear-- } else viewMonth--
                }) {
                    Icon(Icons.Default.ChevronLeft, null, tint = TextPrimary)
                }
                Text("${fullMonths[viewMonth]} $viewYear",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary))
                IconButton(onClick = {
                    if (viewMonth == 11) { viewMonth = 0; viewYear++ } else viewMonth++
                }) {
                    Icon(Icons.Default.ChevronRight, null, tint = TextPrimary)
                }
            }

            // ── Weekday headers
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                weekDays.forEach { wd ->
                    Text(wd, modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary))
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Calendar grid
            val firstDow = firstDayOfWeek(viewYear, viewMonth)
            val totalDays = daysInMonth(viewYear, viewMonth)
            val cells = firstDow + totalDays
            val rows = (cells + 6) / 7

            Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                for (row in 0 until rows) {
                    Row(Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val idx = row * 7 + col
                            val dayNum = idx - firstDow + 1
                            if (dayNum < 1 || dayNum > totalDays) {
                                Box(Modifier.weight(1f).height(38.dp))
                            } else {
                                val dt = CalDate(viewYear, viewMonth, dayNum)
                                val isStart = dt.isSameDay(startDate)
                                val isEnd   = dt.isSameDay(endDate)
                                val inRange = startDate != null && endDate != null && dt.isAfter(startDate!!) && dt.isBefore(endDate!!)
                                val isToday = dt.isSameDay(today)

                                val bgColor = when {
                                    isStart || isEnd -> Purple600
                                    inRange          -> Purple50
                                    else             -> Color.Transparent
                                }
                                val textColor = when {
                                    isStart || isEnd -> Color.White
                                    inRange          -> Purple600
                                    else             -> TextPrimary
                                }
                                val shape = when {
                                    isStart && isEnd                                       -> CircleShape
                                    isStart && endDate != null && !isStart.not()           -> RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp, topEnd = 0.dp, bottomEnd = 0.dp)
                                    isEnd && startDate != null                             -> RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 50.dp, bottomEnd = 50.dp)
                                    isStart                                                -> CircleShape
                                    else                                                   -> RoundedCornerShape(0.dp)
                                }

                                Box(
                                    modifier = Modifier.weight(1f).height(38.dp)
                                        .background(if (isStart || isEnd) Color.Transparent else bgColor)
                                        .clickable {
                                            activePreset = "custom"
                                            if (picking == "start") {
                                                startDate = dt; endDate = null; picking = "end"
                                            } else {
                                                if (startDate != null && dt.isBefore(startDate!!)) { startDate = dt; picking = "end" }
                                                else { endDate = dt; picking = "start" }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (inRange) {
                                        Box(Modifier.fillMaxSize().background(Purple50))
                                    }
                                    if (isStart || isEnd) {
                                        Box(Modifier.size(36.dp).clip(CircleShape).background(Purple600))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            dayNum.toString(),
                                            style = TextStyle(fontSize = 13.sp,
                                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = textColor),
                                            textAlign = TextAlign.Center
                                        )
                                        if (isToday && !isStart && !isEnd) {
                                            Box(Modifier.size(4.dp).clip(CircleShape).background(Purple600))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Footer buttons
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 0.5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Cancel", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick = { if (startDate != null && endDate != null) onApply(startDate!!.toMillis(), endDate!!.toMillis()) },
                    enabled = startDate != null && endDate != null,
                    modifier = Modifier.weight(2f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple600,
                        disabledContainerColor = SurfaceGray,
                        disabledContentColor = TextSecondary
                    )
                ) {
                    Text("Apply range", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
