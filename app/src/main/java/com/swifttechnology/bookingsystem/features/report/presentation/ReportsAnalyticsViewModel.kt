package com.swifttechnology.bookingsystem.features.report.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.core.utils.DateTimeUtils
import com.swifttechnology.bookingsystem.features.report.data.dtos.ReportDataRequestDTO
import com.swifttechnology.bookingsystem.features.report.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.Uri
import javax.inject.Inject

@HiltViewModel
class ReportsAnalyticsViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private var allEntries by mutableStateOf<List<ActivityEntry>>(emptyList())

    init {
        fetchReports()
    }

    private fun fetchReports() {
        viewModelScope.launch {
            isLoading = true
            error = null
            
            repository.getAllReports()
                .onSuccess { dtoList ->
                    allEntries = dtoList.map { dto ->
                        ActivityEntry(
                            meetingTitle = dto.meetingTitle ?: "Untitled",
                            date = dto.date ?: "Unknown Date",
                            startTime = dto.startTime ?: "",
                            endTime = dto.endTime ?: "",
                            roomName = dto.roomName ?: "Unknown Room",
                            createdBy = dto.createdBy ?: "System"
                        )
                    }
                }
                .onFailure { th ->
                    error = th.message ?: "Failed to fetch reports"
                }

            isLoading = false
        }
    }

    // Filter options
    val dateOptions:       List<String> get() = listOf("All") + allEntries.map { it.date }.distinct().sorted()
    val roomOptions:       List<String> get() = listOf("All") + allEntries.map { it.roomName }.distinct().sorted()
    // Using createdBy as a department/user substitute since the backend removed department
    val departmentOptions: List<String> get() = listOf("All") + allEntries.map { it.createdBy }.distinct().sorted()
    val sortOptions:       List<String> = listOf("Asc", "Desc")

    // Filter state
    var selectedDate       by mutableStateOf("All")
    var selectedRoom       by mutableStateOf("All")
    var selectedDepartment by mutableStateOf("All")
    var selectedSort       by mutableStateOf("Asc")

    // Pagination
    val pageSize = 7
    var currentPage by mutableStateOf(0)

    private val filteredEntries: List<ActivityEntry>
        get() {
            var list = allEntries
            if (selectedDate != "All")       list = list.filter { it.date == selectedDate }
            if (selectedRoom != "All")       list = list.filter { it.roomName == selectedRoom }
            if (selectedDepartment != "All") list = list.filter { it.createdBy == selectedDepartment }
            
            list = if (selectedSort == "Asc") list.sortedBy { it.date + it.startTime }
                   else list.sortedByDescending { it.date + it.startTime }
            return list
        }

    val totalResults:  Int      get() = filteredEntries.size
    val totalPages:    Int      get() = maxOf(1, (totalResults + pageSize - 1) / pageSize)
    val showingStart:  Int      get() = if (totalResults == 0) 0 else currentPage * pageSize + 1
    val showingEnd:    Int      get() = minOf((currentPage + 1) * pageSize, totalResults)
    val canGoPrevious: Boolean  get() = currentPage > 0
    val canGoNext:     Boolean  get() = currentPage + 1 < totalPages

    val visibleEntries: List<ActivityEntry>
        get() {
            val start = currentPage * pageSize
            val end   = minOf(start + pageSize, filteredEntries.size)
            return if (start < filteredEntries.size) filteredEntries.subList(start, end) else emptyList()
        }

    fun goToPage(page: Int)             { currentPage = page.coerceIn(0, totalPages - 1) }
    fun onPreviousPage()                { if (canGoPrevious) currentPage-- }
    fun onNextPage()                    { if (canGoNext)     currentPage++ }
    fun onDateSelected(v: String)       { selectedDate = v;       currentPage = 0 }
    fun onRoomSelected(v: String)       { selectedRoom = v;       currentPage = 0 }
    fun onDepartmentSelected(v: String) { selectedDepartment = v; currentPage = 0 }
    fun onSortSelected(v: String)       { selectedSort = v;       currentPage = 0 }

    fun saveExportToUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        // Write CSV Header
                        out.write("Meeting Title,Date,Start Time,End Time,Room,Created By\n".toByteArray())
                        
                        // Write each row, replacing commas to prevent CSV breakage
                        filteredEntries.forEach { e ->
                            val title = e.meetingTitle.replace(",", " -")
                            val room = e.roomName.replace(",", " ")
                            val author = e.createdBy.replace(",", " ")
                            out.write("$title,${e.date},${e.startTime},${e.endTime},$room,$author\n".toByteArray())
                        }
                    }
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to export CSV"
            }
            isLoading = false
        }
    }
}
