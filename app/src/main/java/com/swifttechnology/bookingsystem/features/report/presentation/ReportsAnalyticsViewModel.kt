package com.swifttechnology.bookingsystem.features.report.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.core.utils.DateTimeUtils
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportDataRequestDTO
import com.swifttechnology.bookingsystem.features.report.domain.repository.ReportRepository
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId

@HiltViewModel
class ReportsAnalyticsViewModel @Inject constructor(
    private val repository: ReportRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var allEntries by mutableStateOf<List<ActivityEntry>>(emptyList())
        private set

    var meetingTypes by mutableStateOf<List<MeetingTypeDTO>>(emptyList())
        private set

    var totalElements by mutableStateOf(0L)
        private set

    var totalPages by mutableIntStateOf(1)
        private set

    // Pagination
    val pageSize = 10
    var currentPage by mutableIntStateOf(0)

    // Filter state
    var selectedDate       by mutableStateOf("All")
    var customStartDate    by mutableStateOf<Long?>(null)
    var customEndDate      by mutableStateOf<Long?>(null)
    var selectedRoom       by mutableStateOf("All")
    var selectedUser by mutableStateOf("All")
    var selectedSort       by mutableStateOf("Asc")
    var selectedMeetingType by mutableStateOf<MeetingTypeDTO?>(null)

    init {
        fetchMeetingTypes()
        fetchReports()
    }

    private fun fetchMeetingTypes() {
        viewModelScope.launch {
            bookingRepository.getMeetingTypes()
                .onSuccess { meetingTypes = it }
        }
    }

    fun fetchReports() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val request = ReportDataRequestDTO(
                pageNo = currentPage,
                pageSize = pageSize,
                sortBy = "date",
                sortDir = selectedSort.lowercase(),
                startDate = getFormattedStartDate(),
                endDate = getFormattedEndDate(),
                meetingTypeId = selectedMeetingType?.id,
                roomName = if (selectedRoom == "All") null else selectedRoom,
                createdBy = if (selectedUser == "All") null else selectedUser
            )
            
            repository.getFilteredReports(request)
                .onSuccess { page ->
                    allEntries = page.content?.map { dto ->
                        ActivityEntry(
                            meetingTitle = dto.meetingTitle ?: "Untitled",
                            date = dto.date ?: "Unknown Date",
                            startTime = dto.startTimeString ?: "",
                            endTime = dto.endTimeString ?: "",
                            roomName = dto.roomName ?: "Unknown Room",
                            createdBy = dto.createdBy ?: "System"
                        )
                    } ?: emptyList()
                    totalElements = page.totalElements ?: 0L
                    totalPages = page.totalPages ?: 1
                }
                .onFailure { th ->
                    error = th.message ?: "Failed to fetch reports"
                }

            isLoading = false
        }
    }

    private fun getFormattedStartDate(): String? {
        if (selectedDate == "All") return null
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return when (selectedDate.lowercase()) {
            "today" -> today.format(formatter)
            "7 days" -> today.minusDays(7).format(formatter)
            "30 days" -> today.minusDays(30).format(formatter)
            "custom" -> customStartDate?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate().format(formatter)
            }
            else -> null
        }
    }

    private fun getFormattedEndDate(): String? {
        if (selectedDate == "All") return null
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return when (selectedDate.lowercase()) {
            "today" -> today.format(formatter)
            "7 days", "30 days" -> today.format(formatter)
            "custom" -> customEndDate?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate().format(formatter)
            }
            else -> null
        }
    }

    // Filter options
    val dateOptions:       List<String> get() = listOf("All", "Today", "7 Days", "30 Days", "Custom")
    val roomOptions:       List<String> get() = listOf("All") + allEntries.map { it.roomName }.distinct().sorted()
    // Using createdBy as a department/user substitute since the backend removed department
    val departmentOptions: List<String> get() = listOf("All") + allEntries.map { it.createdBy }.distinct().sorted()
    val sortOptions:       List<String> = listOf("Asc", "Desc")

    
    fun clearFilters() {
        selectedDate = "All"
        customStartDate = null
        customEndDate = null
        selectedRoom = "All"
        selectedUser = "All"
        selectedSort = "Asc"
        selectedMeetingType = null
        currentPage = 0
        fetchReports()
    }


    val totalResults:  Int      get() = totalElements.toInt()
    val showingStart:  Int      get() = if (totalResults == 0) 0 else (currentPage * pageSize + 1)
    val showingEnd:    Int      get() = minOf(((currentPage + 1) * pageSize), totalResults)
    val canGoPrevious: Boolean  get() = currentPage > 0
    val canGoNext:     Boolean  get() = (currentPage + 1) < totalPages

    val visibleEntries: List<ActivityEntry> get() = allEntries

    fun goToPage(page: Int)             { currentPage = page.coerceIn(0, totalPages - 1); fetchReports() }
    fun onPreviousPage()                { if (canGoPrevious) { currentPage--; fetchReports() } }
    fun onNextPage()                    { if (canGoNext)     { currentPage++; fetchReports() } }
    fun onDateSelected(v: String)       { selectedDate = v;       currentPage = 0; fetchReports() }
    fun onRoomSelected(v: String)       { selectedRoom = v;       currentPage = 0; fetchReports() }
    fun onUserSelected(v: String)       { selectedUser = v;      currentPage = 0; fetchReports() }
    fun onSortSelected(v: String)       { selectedSort = v;       currentPage = 0; fetchReports() }
    fun onMeetingTypeSelected(v: MeetingTypeDTO?) { selectedMeetingType = v; currentPage = 0; fetchReports() }

    fun exportCsv(writeAction: suspend (ByteArray) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val request = ReportDataRequestDTO(
                    pageNo = null, // Export all
                    pageSize = null,
                    sortBy = "date",
                    sortDir = selectedSort.lowercase(),
                    startDate = getFormattedStartDate(),
                    endDate = getFormattedEndDate(),
                    meetingTypeId = selectedMeetingType?.id,
                    roomName = if (selectedRoom == "All") null else selectedRoom,
                    createdBy = if (selectedUser == "All") null else selectedUser
                )
                repository.exportReports(request)
                    .onSuccess { responseBody ->
                        val bytes = withContext(Dispatchers.IO) { responseBody.bytes() }
                        writeAction(bytes)
                    }
                    .onFailure { th ->
                        error = th.message ?: "Failed to export reports"
                    }
            } catch (e: Exception) {
                error = e.message ?: "Failed to export CSV"
            } finally {
                isLoading = false
            }
        }
    }
}
