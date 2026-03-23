package com.swifttechnology.bookingsystem.features.booking.presentation

import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class ExternalMember(
    val name: String,
    val email: String
)

data class RoomBookingFormState(
    val meetingTitle: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val meetingType: String = "",
    val participants: List<String> = emptyList(),
    val externalMembers: List<ExternalMember> = emptyList(),
    val selectedRoom: String = "",
    val selectedRoomId: Long? = null
)

data class BookRoomUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.BOOK_ROOM) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.BOOK_ROOM }.copy(isActive = true),
    val searchQuery: String = "",
    val formState: RoomBookingFormState = RoomBookingFormState(),
    val availableRooms: List<Room> = emptyList(),
    val isLoadingRooms: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)
