package com.swifttechnology.bookingsystem.features.booking.presentation

import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class InternalMember(
    val id: Int,
    val name: String,
    val email: String,
    val department: String
)

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
    val recurringType: String = "",
    val description: String = "",
    val participants: List<InternalMember> = emptyList(),
    val externalMembers: List<ExternalMember> = emptyList(),
    val selectedRoom: String = "",
    val selectedRoomId: Long? = null
)

data class BookRoomUiState(
    val searchQuery: String = "",
    val formState: RoomBookingFormState = RoomBookingFormState(),
    val availableRooms: List<Room> = emptyList(),
    val availableParticipants: List<InternalMember> = emptyList(),
    val isLoadingRooms: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)
