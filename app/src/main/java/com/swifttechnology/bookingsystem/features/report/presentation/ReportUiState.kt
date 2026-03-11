package com.swifttechnology.bookingsystem.features.report.presentation

import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

data class ReportUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.REPORT) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.REPORT }.copy(isActive = true),
    val searchQuery: String = ""
)

