package com.swifttechnology.bookingsystem.features.participants.presentation

import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class ParticipantsUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.PARTICIPANTS) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.PARTICIPANTS }.copy(isActive = true),
    val searchQuery: String = ""
)

