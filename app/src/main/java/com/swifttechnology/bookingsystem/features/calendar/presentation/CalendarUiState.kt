package com.swifttechnology.bookingsystem.features.calendar.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
data class CalendarUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.CALENDAR) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.CALENDAR }.copy(isActive = true),
    val searchQuery: String = "",
    val currentView: CalendarView = CalendarView.MONTH,
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedEvent: MeetingEvent? = null,
    val selectedRoom: Room? = null,
    val events: List<MeetingEvent> = emptyList()
)

enum class CalendarView { MONTH, WEEK, DAY }

@RequiresApi(Build.VERSION_CODES.O)
data class MeetingEvent(
    val id: Int,
    val title: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val color: Color,
    val participants: List<String> = emptyList(),
    val description: String = "",
    val meetingRoom: String = "",
    val createdBy: String = "",
    val timeZone: String = "Time Zone",
    val repeats: Boolean = false
)

