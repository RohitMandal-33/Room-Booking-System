package com.swifttechnology.bookingsystem.features.participants.presentation

import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

enum class ParticipantsTab { ALL_PARTICIPANTS, CUSTOM_GROUPS }

data class ParticipantsUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.PARTICIPANTS) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.PARTICIPANTS }.copy(isActive = true),
    val searchQuery: String = "",
    val selectedTab: ParticipantsTab = ParticipantsTab.ALL_PARTICIPANTS,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val participants: List<Participant> = emptyList(),
    val customGroups: List<com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup> = emptyList(),
    val expandedCustomGroupId: Long? = null,
    val resolvedUserNames: Map<Long, String> = emptyMap()
)
