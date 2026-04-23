package com.swifttechnology.bookingsystem.features.calendar.presentation

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.annotation.RequiresApi
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.TimeRange
import com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository.RoomRepository
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.core.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val roomRepository: RoomRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val _uiState = MutableStateFlow(
        CalendarUiState(
            selectedRoom = null,
            events = emptyList() // Real events loaded from API; no placeholder data
        )
    )
    @RequiresApi(Build.VERSION_CODES.O)
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    init {
        loadRooms()
        refreshBookings()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadRooms() {
        viewModelScope.launch {
            roomRepository.listActiveRooms()
                .onSuccess { page ->
                    val roomList = page.content?.map { dto ->
                        Room(
                            id = dto.id,
                            name = dto.roomName,
                            status = RoomStatus.fromApiString(dto.status),
                            capacity = dto.capacity,
                            resources = dto.resources ?: emptyList()
                        )
                    } ?: emptyList()
                    _rooms.value = roomList
                    // Note: We don't auto-select the first room here anymore to allow "All Rooms" view by default
                    // if roomList.isNotEmpty() {
                    //     _uiState.update { it.copy(selectedRoom = roomList.first()) }
                    //     refreshBookings()
                    // }
                }
                .onFailure { /* Keep empty rooms on failure */ }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshBookings() {
        val selectedRoom = _uiState.value.selectedRoom
        val view = _uiState.value.currentView
        val dateStr = _uiState.value.selectedDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)

        viewModelScope.launch {
            val result = when (view) {
                CalendarView.MONTH -> bookingRepository.getCalendarMonth(dateStr)
                CalendarView.WEEK -> bookingRepository.getCalendarWeek(dateStr)
                CalendarView.DAY -> bookingRepository.getCalendarDay(dateStr)
            }

            result.onSuccess { bookings ->
                val events = bookings.mapIndexedNotNull { index, dto ->
                    try {
                        val date = LocalDate.parse(dto.date ?: return@mapIndexedNotNull null)
                        val startTimeStr = dto.startTimeString ?: return@mapIndexedNotNull null
                        val endTimeStr = dto.endTimeString ?: return@mapIndexedNotNull null

                        val startTime = DateTimeUtils.parseLocalTime(startTimeStr) ?: return@mapIndexedNotNull null
                        val endTime = DateTimeUtils.parseLocalTime(endTimeStr) ?: return@mapIndexedNotNull null

                        val color = when (dto.meetingType) {
                            "Internal" -> Color(0xFF4CD8A8)
                            "Client" -> Color(0xFFFF8C5A)
                            "Executive" -> Color(0xFF4A90E2)
                            else -> Color(0xFF4CD8A8)
                        }

                        val backendColor = dto.meetingTypeColorCode?.let {
                            val match = Regex("""\((\d+),\s*(\d+),\s*(\d+)\)""").find(it)
                            if (match != null) {
                                val (r, g, b) = match.destructured
                                Color(r.toInt(), g.toInt(), b.toInt())
                            } else null
                        }

                        MeetingEvent(
                            id = (dto.meetingId ?: dto.id ?: index.toLong()).toInt(),
                            meetingId = dto.meetingId ?: dto.id,
                            title = dto.meetingTitle ?: "Meeting",
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            color = color,
                            backendColor = backendColor,
                            description = dto.description ?: "",
                            meetingRoom = dto.roomName ?: dto.room?.roomName ?: "",
                            meetingType = dto.meetingType ?: "",
                            internalParticipants = dto.internalParticipant ?: emptyList(),
                            externalParticipants = dto.externalParticipant ?: emptyList(),
                            createdBy = dto.roomBooker?.email ?: dto.roomBooker?.firstName ?: "",
                            meetingStatus = dto.meetingStatus ?: dto.status
                        )
                    } catch (e: Exception) {
                        null
                    }
                }.filter { event ->
                    selectedRoom == null || event.meetingRoom.equals(selectedRoom.name, ignoreCase = true)
                }
                _uiState.update { it.copy(events = events) }
            }.onFailure {
                _uiState.update { it.copy(events = emptyList()) }
            }
        }
    }

    fun onSidebarItemSelected(item: SidebarItem) {
        _uiState.update { current ->
            current.copy(
                sidebarItems = current.sidebarItems.map { it.copy(isActive = it.route == item.route) },
                selectedItem = item.copy(isActive = true)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onViewChange(view: CalendarView) {
        _uiState.update { it.copy(currentView = view) }
        refreshBookings()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onPrev() {
        _uiState.update { state ->
            when (state.currentView) {
                CalendarView.MONTH -> {
                    val newMonth = state.currentMonth.minusMonths(1)
                    val newDay = min(state.selectedDate.dayOfMonth, newMonth.lengthOfMonth())
                    state.copy(
                        currentMonth = newMonth,
                        selectedDate = newMonth.atDay(newDay)
                    )
                }
                CalendarView.WEEK -> state.copy(selectedDate = state.selectedDate.minusDays(7))
                CalendarView.DAY -> state.copy(selectedDate = state.selectedDate.minusDays(1))
            }
        }
        refreshBookings()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onNext() {
        _uiState.update { state ->
            when (state.currentView) {
                CalendarView.MONTH -> {
                    val newMonth = state.currentMonth.plusMonths(1)
                    val newDay = min(state.selectedDate.dayOfMonth, newMonth.lengthOfMonth())
                    state.copy(
                        currentMonth = newMonth,
                        selectedDate = newMonth.atDay(newDay)
                    )
                }
                CalendarView.WEEK -> state.copy(selectedDate = state.selectedDate.plusDays(7))
                CalendarView.DAY -> state.copy(selectedDate = state.selectedDate.plusDays(1))
            }
        }
        refreshBookings()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                currentMonth = YearMonth.from(date)
            )
        }
        refreshBookings()
    }

    fun onEventSelected(event: MeetingEvent?) {
        _uiState.update { it.copy(selectedEvent = event) }
        if (event != null && event.meetingId != null) {
            viewModelScope.launch {
                bookingRepository.getBookingDetails(event.meetingId)
                    .onSuccess { dto ->
                        val updatedEvent = event.copy(
                            description = dto.description ?: "",
                            internalParticipants = dto.internalParticipant ?: emptyList(),
                            externalParticipants = dto.externalParticipant ?: emptyList(),
                            createdBy = dto.roomBooker?.email ?: dto.roomBooker?.firstName ?: "Unknown",
                            meetingStatus = dto.meetingStatus ?: dto.status,
                            meetingRoom = dto.room?.roomName ?: dto.roomName ?: event.meetingRoom
                        )
                        _uiState.update { 
                            if (it.selectedEvent?.meetingId == event.meetingId) {
                                it.copy(selectedEvent = updatedEvent)
                            } else it
                        }
                    }
            }
        }
    }

    fun onRoomSelected(room: Room) {
        _uiState.update { it.copy(selectedRoom = room) }
        refreshBookings()
    }

    suspend fun logout() {
        authRepository.logout()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBlockedSlotsForDate(date: LocalDate): List<TimeRange> {
        return _uiState.value.events
            .filter { it.date == date }
            .map { event ->
                val startMin = event.startTime.hour * 60 + event.startTime.minute
                val endMin = event.endTime.hour * 60 + event.endTime.minute
                TimeRange(startMin, endMin)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBlockedSlotsForRoomAndDate(roomName: String, date: LocalDate): List<TimeRange> {
        return _uiState.value.events
            .filter { it.date == date && it.meetingRoom.equals(roomName, ignoreCase = true) }
            .map { event ->
                val startMin = event.startTime.hour * 60 + event.startTime.minute
                val endMin = event.endTime.hour * 60 + event.endTime.minute
                TimeRange(startMin, endMin)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventsForRoomAndDate(roomName: String, date: LocalDate): List<MeetingEvent> {
        return _uiState.value.events.filter { 
            it.meetingRoom.equals(roomName, ignoreCase = true) && it.date == date 
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun sampleEvents(): List<MeetingEvent> {
    val eventGreen = Color(0xFF4CD8A8)
    val eventOrange = Color(0xFFFF8C5A)
    val eventBlue = Color(0xFF4A90E2)

    // Keep sample data deterministic but aligned to current month
    val now = LocalDate.now()
    val ym = YearMonth.from(now)
    fun d(day: Int) = ym.atDay(day.coerceIn(1, ym.lengthOfMonth()))

    return listOf(
        MeetingEvent(id = 1, title = "Team Sync", date = d(5), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0), color = eventGreen),
        MeetingEvent(id = 2, title = "Design Review", date = d(7), startTime = LocalTime.of(14, 0), endTime = LocalTime.of(15, 0), color = eventOrange),
        MeetingEvent(id = 3, title = "Sprint Planning", date = d(9), startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 30), color = eventGreen),
        MeetingEvent(id = 4, title = "1:1 Meeting", date = d(13), startTime = LocalTime.of(11, 0), endTime = LocalTime.of(12, 0), color = eventGreen),
        MeetingEvent(id = 5, title = "Product Demo", date = d(20), startTime = LocalTime.of(15, 0), endTime = LocalTime.of(16, 0), color = eventOrange),
        MeetingEvent(
            id = 6,
            title = "Board Review",
            date = d(23),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            color = eventBlue,
            description = "This is the description about the meeting",
            meetingRoom = "Conference Room A",
            createdBy = "John Doe",
            timeZone = "Time Zone",
            repeats = false
        ),
        MeetingEvent(id = 7, title = "Weekly Standup", date = d(27), startTime = LocalTime.of(9, 0), endTime = LocalTime.of(9, 30), color = eventOrange)
    )
}
