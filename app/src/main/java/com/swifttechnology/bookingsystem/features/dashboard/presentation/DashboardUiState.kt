package com.swifttechnology.bookingsystem.features.dashboard.presentation

import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement

data class DashboardUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.DASHBOARD) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.DASHBOARD }.copy(isActive = true),
    val searchQuery: String = "",
    val isLoadingMeetings: Boolean = false,
    val upcomingMeetings: List<BookingResponseDTO> = emptyList(),
    val calendarMeetings: List<BookingResponseDTO> = emptyList(),
    val isLoadingAnnouncements: Boolean = false,
    val isLoadingCalendar: Boolean = false,
    val announcements: List<Announcement> = emptyList(),
    val customStartDate: Long? = null,
    val customEndDate: Long? = null,
    val error: String? = null,
    val dashboardStats: com.swifttechnology.bookingsystem.features.dashboard.data.dtos.DashboardResponseDTO? = null
)

