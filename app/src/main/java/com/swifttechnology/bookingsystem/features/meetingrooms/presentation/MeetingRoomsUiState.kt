package com.swifttechnology.bookingsystem.features.meetingrooms.presentation

import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class MeetingRoomsUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == com.swifttechnology.bookingsystem.navigation.ScreenRoutes.MEETING_ROOMS) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == com.swifttechnology.bookingsystem.navigation.ScreenRoutes.MEETING_ROOMS }
        .copy(isActive = true),
    val rooms: List<Room> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val filteredRooms: List<Room>
        get() = if (searchQuery.isBlank()) rooms
        else rooms.filter { it.name.contains(searchQuery, ignoreCase = true) }
}
