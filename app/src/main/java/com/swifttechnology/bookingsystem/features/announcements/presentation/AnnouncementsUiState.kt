package com.swifttechnology.bookingsystem.features.announcements.presentation

import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class AnnouncementsUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.ANNOUNCEMENTS) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.ANNOUNCEMENTS }.copy(isActive = true),
    val searchQuery: String = ""
)

