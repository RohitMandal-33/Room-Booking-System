package com.swifttechnology.bookingsystem.shared.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.Spacing

data class SidebarItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val defaultSidebarItems = listOf(
    SidebarItem("Dashboard", Icons.Default.Dashboard, "dashboard"),
    SidebarItem("Calendar", Icons.Default.CalendarMonth, "calendar"),
    SidebarItem("Rooms", Icons.Default.MeetingRoom, "rooms"),
    SidebarItem("My Bookings", Icons.Default.BookOnline, "bookings"),
    SidebarItem("Reports", Icons.Default.BarChart, "reports"),
    SidebarItem("Announcements", Icons.AutoMirrored.Filled.Announcement, "announcements"),
    SidebarItem("Settings", Icons.Default.Person, "settings")
)


@Composable
fun SidebarView(
    drawerState: DrawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed),
    selectedRoute: String = "dashboard",
    items: List<SidebarItem> = defaultSidebarItems,
    onItemClick: (SidebarItem) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                Text(
                    text = "Meeting Room Booking",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(Spacing.md)
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(Spacing.sm))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = item.route == selectedRoute,
                        onClick = { onItemClick(item) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun SidebarViewPreview() {
    SidebarView(onItemClick = {}) {
        Text("Content")
    }
}
