package com.swifttechnology.bookingsystem.features.settings.presentation

import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class SettingsUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.SETTINGS) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.SETTINGS }.copy(isActive = true),
    val searchQuery: String = ""
)

