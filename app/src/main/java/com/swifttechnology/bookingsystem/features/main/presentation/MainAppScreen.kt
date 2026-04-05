package com.swifttechnology.bookingsystem.features.main.presentation

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.swifttechnology.bookingsystem.features.participants.presentation.AddParticipantScreen
import com.swifttechnology.bookingsystem.features.report.presentation.ReportScreen
import com.swifttechnology.bookingsystem.features.settings.presentation.SettingsScreen
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.defaultSidebarItems
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import com.swifttechnology.bookingsystem.shared.components.DialogStyle
import com.swifttechnology.bookingsystem.shared.components.ReusableAlertDialog
import com.swifttechnology.bookingsystem.features.booking.presentation.RoomCalendarScreen
import com.swifttechnology.bookingsystem.core.model.Room
import com.swifttechnology.bookingsystem.shared.layout.BottomSheetView
import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant

data class PendingBookingDetails(
    val roomName: String,
    val date: String,
    val startTime: String,
    val endTime: String
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAppScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    startRoute: String = ScreenRoutes.DASHBOARD
) {
    var navigationStack by rememberSaveable { mutableStateOf(listOf(startRoute)) }
    val currentRoute = navigationStack.last()

    val navigateTo: (String) -> Unit = { route ->
        if (navigationStack.last() != route) {
            navigationStack = navigationStack + route
        }
    }

    val navigateBack: () -> Unit = {
        if (navigationStack.size > 1) {
            navigationStack = navigationStack.dropLast(1)
        }
    }

    var showRoomCalendarBottomSheet by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isMeetingRoomsEditable by rememberSaveable { mutableStateOf(false) }
    var isParticipantsEditable by rememberSaveable { mutableStateOf(false) }
    var pendingRoomName by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingBookingDetails by remember { mutableStateOf<PendingBookingDetails?>(null) }
    var pendingParticipantToEdit by remember { mutableStateOf<Participant?>(null) }

    val sidebarItems = defaultSidebarItems.map {
        it.copy(isActive = it.route == currentRoute)
    }

    val selectedItem = sidebarItems.firstOrNull { it.isActive } ?: sidebarItems.first()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    BackHandler {
        if (showRoomCalendarBottomSheet) {
            showRoomCalendarBottomSheet = false
        } else if (navigationStack.size > 1) {
            navigateBack()
        } else if (currentRoute != ScreenRoutes.DASHBOARD) {
            navigationStack = listOf(ScreenRoutes.DASHBOARD)
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

    val screenTitle = when (currentRoute) {
        ScreenRoutes.DASHBOARD -> "Dashboard"
        ScreenRoutes.CALENDAR -> "Calendar"
        ScreenRoutes.ROOM_CALENDAR -> "Room Calendar"
        ScreenRoutes.BOOK_ROOM -> "Book Room"
        ScreenRoutes.MEETING_ROOMS -> "Meeting Rooms"
        ScreenRoutes.ANNOUNCEMENTS -> "Announcements"
        ScreenRoutes.REPORT -> "Report"
        ScreenRoutes.PARTICIPANTS -> "Members"
        ScreenRoutes.SETTINGS -> "Settings"
        else -> "Dashboard"
    }

    MainScaffold(
        sidebarItems = sidebarItems,
        selectedItem = selectedItem,
        title = screenTitle,
        onSidebarItemSelected = { item ->
            navigationStack = listOf(item.route)
            searchQuery = "" // Reset search when switching tabs
            pendingRoomName = null // Clear pending room when manually switching tabs
            pendingBookingDetails = null
        },
        onLogout = onLogout,
        showEditIcon = currentRoute == ScreenRoutes.MEETING_ROOMS || currentRoute == ScreenRoutes.PARTICIPANTS,
        showTopBar = currentRoute != ScreenRoutes.ROOM_CALENDAR && currentRoute != ScreenRoutes.PARTICIPANT_ADD,
        onEditClick = {
            if (currentRoute == ScreenRoutes.MEETING_ROOMS) {
                isMeetingRoomsEditable = !isMeetingRoomsEditable
            } else if (currentRoute == ScreenRoutes.PARTICIPANTS) {
                isParticipantsEditable = !isParticipantsEditable
            }
        },
        onBackClick = if (navigationStack.size > 1) navigateBack else null
    ) {
        when (currentRoute) {
            ScreenRoutes.DASHBOARD -> {
                DashboardScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo
                )
            }
            ScreenRoutes.CALENDAR -> {
                CalendarScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo
                )
            }
            ScreenRoutes.MEETING_ROOMS -> {
                MeetingRoomsScreen(
                    searchQuery = searchQuery,
                    isEditable = isMeetingRoomsEditable,
                    onNavigate = navigateTo,
                    onOpenRoomEdit = { roomName ->
                        navController.navigate(ScreenRoutes.meetingRoomEdit(roomName))
                    },
                    onNavigateToAddRoom = {
                        navController.navigate(ScreenRoutes.MEETING_ROOM_ADD)
                    },
                    onBookClick = { room ->
                        pendingRoomName = room.name
                        showRoomCalendarBottomSheet = true
                    }
                )
            }
            ScreenRoutes.ROOM_CALENDAR -> {
                // This route is now handled via BottomSheet, but we keep it here for safety
                // or redirect to Dashboard if it somehow ends up here.
                DashboardScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo
                )
            }
            ScreenRoutes.BOOK_ROOM -> {
                BookRoomScreen(
                    searchQuery = searchQuery,
                    initialRoomName = pendingRoomName,
                    initialDetails = pendingBookingDetails,
                    onNavigateToCalendar = { currentDetails ->
                        pendingBookingDetails = currentDetails
                        showRoomCalendarBottomSheet = true
                    },
                    onNavigate = navigateTo,
                    onNavigateToEdit = {
                        navigationStack = listOf(ScreenRoutes.MEETING_ROOMS)
                        isMeetingRoomsEditable = true
                    }
                )
            }
            ScreenRoutes.ANNOUNCEMENTS -> {
                AnnouncementsScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo
                )
            }
            ScreenRoutes.REPORT -> {
                ReportScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo
                )
            }
            ScreenRoutes.PARTICIPANTS -> {
                ParticipantsScreen(
                    searchQuery = searchQuery,
                    isEditable = isParticipantsEditable,
                    onNavigate = navigateTo,
                    onEditSelected = { participant ->
                        pendingParticipantToEdit = participant
                        navigateTo(ScreenRoutes.PARTICIPANT_ADD)
                    }
                )
            }
            ScreenRoutes.PARTICIPANT_ADD -> {
                AddParticipantScreen(
                    initialParticipant = pendingParticipantToEdit,
                    onClose = {
                        pendingParticipantToEdit = null
                        navigateBack()
                    },
                    onContinue = {
                        pendingParticipantToEdit = null
                        navigateBack()
                    }
                )
            }
            ScreenRoutes.SETTINGS -> {
                SettingsScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo
                )
            }
            else -> {
                DashboardScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo
                )
            }
        }
    }

    if (showRoomCalendarBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        BottomSheetView(
            onDismiss = { showRoomCalendarBottomSheet = false },
            sheetState = sheetState
        ) {
            RoomCalendarScreen(
                roomName = pendingRoomName,
                initialDetails = pendingBookingDetails,
                onNavigateBack = { showRoomCalendarBottomSheet = false },
                onProceed = { details ->
                    pendingBookingDetails = details
                    showRoomCalendarBottomSheet = false
                    navigateTo(ScreenRoutes.BOOK_ROOM)
                }
            )
        }
    }
}
