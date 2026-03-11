package com.swifttechnology.bookingsystem.features.booking.presentation

import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class RoomBookingFormState(
    val meetingTitle: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val meetingType: String = "",
    val participants: List<String> = emptyList(),
    val selectedRoom: String = ""
)

data class BookRoomUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.BOOK_ROOM) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.BOOK_ROOM }.copy(isActive = true),
    val searchQuery: String = "",
    val formState: RoomBookingFormState = RoomBookingFormState()
)

