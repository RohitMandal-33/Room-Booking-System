package com.swifttechnology.bookingsystem.features.booking.presentation

import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class InternalMember(
    val id: Long,
    val name: String,
    val email: String,
    val department: String
)

data class ExternalMember(
    val name: String,
    val email: String
)

data class RoomBookingFormState(
    val bookingId: Long? = null,
    val meetingTitle: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val meetingType: String = "",
    val meetingTypeId: Long? = null,
    val recurringType: String = "Does not repeat",
    val recurrenceEndDate: String = "",
    val selectedWeekDays: Set<String> = emptySet(),
    val description: String = "",
    val participants: List<InternalMember> = emptyList(),
    /** Tracks which custom-group IDs were explicitly selected via the Groups tab.
     *  Independent of individual member selection in the People / Teams tabs. */
    val selectedGroupIds: Set<Long> = emptySet(),
    val externalMembers: List<ExternalMember> = emptyList(),
    val selectedRoom: String = "",
    val selectedRoomId: Long? = null,
    val updateScope: String = "ALL"
)

data class BookRoomUiState(
    val searchQuery: String = "",
    val participantSearchQuery: String = "",
    val formState: RoomBookingFormState = RoomBookingFormState(),
    val availableRooms: List<Room> = emptyList(),
    val availableParticipants: List<InternalMember> = emptyList(),
    val availableMeetingTypes: List<MeetingTypeDTO> = emptyList(),
    val isLoadingRooms: Boolean = false,
    val isSearchingParticipants: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null
)
