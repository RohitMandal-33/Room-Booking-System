package com.swifttechnology.bookingsystem.features.main.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.swifttechnology.bookingsystem.features.announcements.presentation.AnnouncementsScreen
import com.swifttechnology.bookingsystem.features.booking.presentation.BookRoomScreen
import com.swifttechnology.bookingsystem.features.calendar.presentation.CalendarScreen
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomsScreen
import com.swifttechnology.bookingsystem.features.participants.presentation.ParticipantsScreen
import com.swifttechnology.bookingsystem.features.report.presentation.ReportScreen
import com.swifttechnology.bookingsystem.features.settings.presentation.SettingsScreen
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import com.swifttechnology.bookingsystem.shared.components.DialogStyle
import com.swifttechnology.bookingsystem.shared.components.ReusableAlertDialog

@Composable
fun MainAppScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    startRoute: String = ScreenRoutes.DASHBOARD
) {
    var currentRoute by rememberSaveable { mutableStateOf(startRoute) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isMeetingRoomsEditable by rememberSaveable { mutableStateOf(false) }

    val sidebarItems = defaultSidebarItems.map {
        it.copy(isActive = it.route == currentRoute)
    }

    val selectedItem = sidebarItems.firstOrNull { it.isActive } ?: sidebarItems.first()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    BackHandler {
        if (currentRoute != ScreenRoutes.DASHBOARD) {
            currentRoute = ScreenRoutes.DASHBOARD
            searchQuery = ""
        } else {
            showExitDialog = true
        }
    }

    if (showExitDialog) {
        ReusableAlertDialog(
            style = DialogStyle.WARNING,
            title = "Exit App",
            message = "Are you sure you want to exit?",
            confirmText = "Exit",
            onConfirm = {
                showExitDialog = false
                activity?.finish()
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }

    MainScaffold(
        sidebarItems = sidebarItems,
        selectedItem = selectedItem,
        searchQuery = searchQuery,
        onSearchQueryChanged = { searchQuery = it },
        onSidebarItemSelected = { item ->
            currentRoute = item.route
            searchQuery = "" // Reset search when switching tabs
        },
        onLogout = onLogout,
        showEditIcon = currentRoute == ScreenRoutes.MEETING_ROOMS,
        onEditClick = {
            if (currentRoute == ScreenRoutes.MEETING_ROOMS) {
                isMeetingRoomsEditable = !isMeetingRoomsEditable
            }
        }
    ) {
        when (currentRoute) {
            ScreenRoutes.DASHBOARD -> {
                DashboardScreen(
                    searchQuery = searchQuery,
                    onNavigate = { currentRoute = it }
                )
            }
            ScreenRoutes.CALENDAR -> {
                CalendarScreen(
                    searchQuery = searchQuery,
                    onNavigate = { currentRoute = it }
                )
            }
            ScreenRoutes.MEETING_ROOMS -> {
                MeetingRoomsScreen(
                    searchQuery = searchQuery,
                    isEditable = isMeetingRoomsEditable,
                    onNavigate = { currentRoute = it },
                    onOpenRoomEdit = { roomName ->
                        navController.navigate(ScreenRoutes.meetingRoomEdit(roomName))
                    }
                )
            }
            ScreenRoutes.BOOK_ROOM -> {
                BookRoomScreen(
                    searchQuery = searchQuery,
                    onNavigate = { currentRoute = it },
                    onNavigateToEdit = {
                        currentRoute = ScreenRoutes.MEETING_ROOMS
                        isMeetingRoomsEditable = true
                    }
                )
            }
            ScreenRoutes.ANNOUNCEMENTS -> {
                AnnouncementsScreen(
                    searchQuery = searchQuery,
                    onNavigate = { currentRoute = it }
                )
            }
            ScreenRoutes.REPORT -> {
                ReportScreen(
                    searchQuery = searchQuery,
                    onNavigate = { currentRoute = it }
                )
            }
            ScreenRoutes.PARTICIPANTS -> {
                ParticipantsScreen(
                    searchQuery = searchQuery,
                    onNavigate = { currentRoute = it }
                )
            }
            ScreenRoutes.SETTINGS -> {
                SettingsScreen(
                    searchQuery = searchQuery,
                    onNavigate = { currentRoute = it }
                )
            }
            else -> {
                DashboardScreen(
                    searchQuery = searchQuery,
                    onNavigate = { currentRoute = it }
                )
            }
        }
    }
}
