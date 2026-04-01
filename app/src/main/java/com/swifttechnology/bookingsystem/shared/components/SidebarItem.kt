package com.swifttechnology.bookingsystem.shared.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentPasteSearch
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes

data class SidebarItem(
    val label: String,
    val icon: ImageVector,
    val isActive: Boolean = false,
    val route: String = ""
)

val defaultSidebarItems = listOf(
    SidebarItem("Dashboard", Icons.Outlined.Dashboard, route = ScreenRoutes.DASHBOARD),
    SidebarItem("Calendar", Icons.Outlined.CalendarMonth, route = ScreenRoutes.CALENDAR),
    SidebarItem("Meeting Rooms", Icons.Outlined.MeetingRoom, isActive = true, route = ScreenRoutes.MEETING_ROOMS),
    SidebarItem("Announcements", Icons.Outlined.NotificationsNone, route = ScreenRoutes.ANNOUNCEMENTS),
    SidebarItem("Report", Icons.Outlined.ContentPasteSearch, route = ScreenRoutes.REPORT),
    SidebarItem("Members", Icons.Outlined.AccountCircle, route = ScreenRoutes.PARTICIPANTS),
    SidebarItem("Settings", Icons.Outlined.Settings, route = ScreenRoutes.SETTINGS)
)

