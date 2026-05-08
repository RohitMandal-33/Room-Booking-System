package com.swifttechnology.bookingsystem.features.settings.presentation

import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import com.swifttechnology.bookingsystem.features.department.domain.model.Department
import com.swifttechnology.bookingsystem.core.designsystem.ThemeMode

data class SettingsUiState(
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map { it.copy(isActive = it.route == ScreenRoutes.SETTINGS) },
    val selectedItem: SidebarItem = defaultSidebarItems.first { it.route == ScreenRoutes.SETTINGS }.copy(isActive = true),
    val searchQuery: String = "",
    val meetingTypes: List<MeetingTypeDTO> = emptyList(),

    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,

    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val departments: List<Department> = emptyList(),
    
    // Global Rules Mock State
    val durationMin: String = "30",
    val bufferMin: String = "5",
    val smartConflict: Boolean = true
)

