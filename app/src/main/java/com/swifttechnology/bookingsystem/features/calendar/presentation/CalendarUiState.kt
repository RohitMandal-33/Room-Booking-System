package com.swifttechnology.bookingsystem.features.calendar.presentation

import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class CalendarUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.CALENDAR) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.CALENDAR }.copy(isActive = true),
    val searchQuery: String = ""
)

