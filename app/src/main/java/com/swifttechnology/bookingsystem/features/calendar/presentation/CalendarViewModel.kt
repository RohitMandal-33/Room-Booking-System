package com.swifttechnology.bookingsystem.features.calendar.presentation

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.annotation.RequiresApi
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.TimeRange
import com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository.RoomRepository
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
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
            events = sampleEvents()
        )
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    init {
        loadRooms()
        loadBookings()
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
                    if (roomList.isNotEmpty()) {
                        _uiState.update { it.copy(selectedRoom = roomList.first()) }
                    }
                }
                .onFailure { /* Keep empty rooms on failure */ }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadBookings() {
        viewModelScope.launch {
            bookingRepository.getAllBookings()
                .onSuccess { bookings ->
                    val events = bookings.mapIndexedNotNull { index, dto ->
                        try {
                            val date = LocalDate.parse(dto.date ?: return@mapIndexedNotNull null)
                            val startTime = dto.startTime?.let {
                                try { LocalTime.parse(it) } catch (e: Exception) { null }
                            } ?: return@mapIndexedNotNull null
                            val endTime = dto.endTime?.let {
                                try { LocalTime.parse(it) } catch (e: Exception) { null }
                            } ?: return@mapIndexedNotNull null

                            val color = when (dto.meetingType) {
                                "INTERNAL" -> Color(0xFF4CD8A8)
                                "CLIENT" -> Color(0xFFFF8C5A)
                                "EXECUTIVE" -> Color(0xFF4A90E2)
                                else -> Color(0xFF4CD8A8)
                            }

                            MeetingEvent(
                                id = (dto.id ?: index.toLong()).toInt(),
                                title = dto.meetingTitle ?: "Meeting",
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                color = color,
                                description = dto.description ?: "",
                                meetingRoom = dto.roomName ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (events.isNotEmpty()) {
                        _uiState.update { it.copy(events = events) }
                    }
                    // If no API events, keep the sample events
                }
                .onFailure { /* Keep sample events on failure */ }
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
                CalendarView.WEEK -> state.copy(selectedDate = state.selectedDate.minusWeeks(1))
                CalendarView.DAY -> state.copy(selectedDate = state.selectedDate.minusDays(1))
            }
        }
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
                CalendarView.WEEK -> state.copy(selectedDate = state.selectedDate.plusWeeks(1))
                CalendarView.DAY -> state.copy(selectedDate = state.selectedDate.plusDays(1))
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                currentMonth = YearMonth.from(date)
            )
        }
    }

    fun onEventSelected(event: MeetingEvent?) {
        _uiState.update { it.copy(selectedEvent = event) }
    }

    fun onRoomSelected(room: Room) {
        _uiState.update { it.copy(selectedRoom = room) }
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
        MeetingEvent(1, "Team Sync", d(5), LocalTime.of(10, 0), LocalTime.of(11, 0), eventGreen),
        MeetingEvent(2, "Design Review", d(7), LocalTime.of(14, 0), LocalTime.of(15, 0), eventOrange),
        MeetingEvent(3, "Sprint Planning", d(9), LocalTime.of(9, 0), LocalTime.of(10, 30), eventGreen),
        MeetingEvent(4, "1:1 Meeting", d(13), LocalTime.of(11, 0), LocalTime.of(12, 0), eventGreen),
        MeetingEvent(5, "Product Demo", d(20), LocalTime.of(15, 0), LocalTime.of(16, 0), eventOrange),
        MeetingEvent(
            6,
            "Board Review",
            d(23),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            eventBlue,
            description = "This is the description about the meeting",
            meetingRoom = "Conference Room A",
            createdBy = "John Doe",
            timeZone = "Time Zone",
            repeats = false
        ),
        MeetingEvent(7, "Weekly Standup", d(27), LocalTime.of(9, 0), LocalTime.of(9, 30), eventOrange)
    )
}
