package com.swifttechnology.bookingsystem.features.announcements.presentation

import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems

enum class AnnouncementTab { ALL, PINNED }

data class AnnouncementsUiState(
    // Navigation
    val sidebarItems: List<SidebarItem> = defaultSidebarItems.map {
        it.copy(isActive = it.route == ScreenRoutes.ANNOUNCEMENTS)
    },
    val selectedItem: SidebarItem = defaultSidebarItems
        .first { it.route == ScreenRoutes.ANNOUNCEMENTS }
        .copy(isActive = true),
    val searchQuery: String = "",

    // Tabs
    val selectedTab: AnnouncementTab = AnnouncementTab.ALL,

    // Data
    val allAnnouncements: List<Announcement> = emptyList(),
    val pinnedAnnouncements: List<Announcement> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Add / Edit bottom sheet
    val showAddEditSheet: Boolean = false,
    val editingAnnouncement: Announcement? = null,   // null = add, non-null = edit

    // Operation snackbar
    val successMessage: String? = null,
    val operationError: String? = null
) {
    /** Items shown for the currently active tab, filtered by search query */
    val displayedAnnouncements: List<Announcement>
        get() {
            val source = when (selectedTab) {
                AnnouncementTab.ALL    -> allAnnouncements
                AnnouncementTab.PINNED -> pinnedAnnouncements
            }
            return if (searchQuery.isBlank()) source
            else source.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.message.contains(searchQuery, ignoreCase = true) ||
                it.authorName.contains(searchQuery, ignoreCase = true)
            }
        }
}
