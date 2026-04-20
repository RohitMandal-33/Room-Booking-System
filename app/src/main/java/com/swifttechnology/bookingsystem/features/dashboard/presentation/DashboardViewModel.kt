package com.swifttechnology.bookingsystem.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import com.swifttechnology.bookingsystem.features.announcements.domain.repository.AnnouncementRepository
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        fetchUpcomingMeetings()
        fetchAnnouncements()
    }

    fun refreshAll() {
        fetchUpcomingMeetings()
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAnnouncements = true) }
            val result = announcementRepository.getTargetedPinnedAnnouncements()
            _uiState.update { 
                it.copy(
                    isLoadingAnnouncements = false,
                    announcements = result.getOrDefault(emptyList()) // we'll update UI state to include this
                )
            }
        }
    }

    private fun fetchUpcomingMeetings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMeetings = true, error = null) }
            bookingRepository.getUpcomingMeetings()
                .onSuccess { meetings ->
                    _uiState.update { it.copy(isLoadingMeetings = false, upcomingMeetings = meetings) }
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoadingMeetings = false, error = exception.message) }
                }
        }
    }

    fun onSidebarItemSelected(item: SidebarItem) {
        _uiState.update { current ->
            current.copy(
                sidebarItems = current.sidebarItems.map {
                    it.copy(isActive = it.route == item.route)
                },
                selectedItem = item.copy(isActive = true)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onDateRangeSelected(start: Long, end: Long) {
        _uiState.update { it.copy(customStartDate = start, customEndDate = end) }
        // If we want to filter the meetings based on this range
        filterMeetings()
    }

    fun clearDateFilter() {
        _uiState.update { it.copy(customStartDate = null, customEndDate = null) }
        fetchUpcomingMeetings() // Reset by fetching again (or just filterMeetings() with nulls)
    }

    private fun filterMeetings() {
        val start = _uiState.value.customStartDate
        val end = _uiState.value.customEndDate
        if (start == null || end == null) return

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val filtered = _uiState.value.upcomingMeetings.filter { meeting ->
            meeting.date?.let { dateStr ->
                try {
                    val date = sdf.parse(dateStr)
                    val time = date?.time ?: 0L
                    time in start..end
                } catch (e: Exception) {
                    true // keep if parse fails?
                }
            } ?: true
        }
        _uiState.update { it.copy(upcomingMeetings = filtered) }
    }

    suspend fun logout() {
        authRepository.logout()
    }
}

