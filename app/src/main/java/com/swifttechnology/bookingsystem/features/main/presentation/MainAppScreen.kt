package com.swifttechnology.bookingsystem.features.main.presentation

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swifttechnology.bookingsystem.features.announcements.presentation.AnnouncementsScreen
import com.swifttechnology.bookingsystem.features.booking.presentation.BookRoomScreen
import com.swifttechnology.bookingsystem.features.calendar.presentation.CalendarScreen
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomsScreen
import com.swifttechnology.bookingsystem.features.participants.presentation.ParticipantsScreen
import com.swifttechnology.bookingsystem.features.participants.presentation.components.AddCustomGroupScreen
import com.swifttechnology.bookingsystem.features.participants.presentation.components.AddParticipantScreen
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
import com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import com.swifttechnology.bookingsystem.features.booking.presentation.ExternalMember
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PendingBookingDetails(
    val roomName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val meetingTitle: String = "",
    val meetingType: String = "",
    val description: String = "",
    val internalParticipantIds: List<Long> = emptyList(),
    val externalMembers: List<ExternalMember> = emptyList()
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
    var pendingCustomGroupToEdit by remember { mutableStateOf<CustomGroup?>(null) }
    // Success toast shown on the Participants screen after add/edit
    var groupSuccessMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(groupSuccessMessage) {
        if (groupSuccessMessage != null) {
            kotlinx.coroutines.delay(2500)
            groupSuccessMessage = null
        }
    }

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
            searchQuery = ""
            pendingRoomName = null
            pendingBookingDetails = null
            isMeetingRoomsEditable = false
            isParticipantsEditable = false
        },
        onLogout = onLogout,
        showEditIcon = currentRoute == ScreenRoutes.MEETING_ROOMS || currentRoute == ScreenRoutes.PARTICIPANTS,
        isEditMode = (currentRoute == ScreenRoutes.MEETING_ROOMS && isMeetingRoomsEditable) ||
            (currentRoute == ScreenRoutes.PARTICIPANTS && isParticipantsEditable),
        showTopBar = currentRoute != ScreenRoutes.ROOM_CALENDAR &&
            currentRoute != ScreenRoutes.PARTICIPANT_ADD &&
            currentRoute != ScreenRoutes.CUSTOM_GROUP_ADD,
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
                    onNavigate = navigateTo,
                    onEditMeeting = { event ->
                        // Pre-fill room name and date/time from the event
                        pendingRoomName = event.meetingRoom.takeIf { it.isNotBlank() }
                        val dateFmt = event.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
                        val startFmt = event.startTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
                        val endFmt   = event.endTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
                        pendingBookingDetails = PendingBookingDetails(
                            roomName               = event.meetingRoom,
                            date                   = dateFmt,
                            startTime              = startFmt,
                            endTime                = endFmt,
                            meetingTitle           = event.title,
                            meetingType            = event.meetingType,
                            description            = event.description,
                            internalParticipantIds = event.internalParticipants.mapNotNull { it.id?.toLong() },
                            externalMembers        = event.externalParticipants.map { ExternalMember(it.name ?: "", it.email ?: "") }
                        )
                        navigateTo(ScreenRoutes.BOOK_ROOM)
                    }
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
                    },
                    onEnterEditMode = {
                        isMeetingRoomsEditable = true
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
                Box(modifier = Modifier.fillMaxSize()) {
                    ParticipantsScreen(
                        searchQuery = searchQuery,
                        isEditable = isParticipantsEditable,
                        onNavigate = navigateTo,
                        onEditSelected = { participant ->
                            pendingParticipantToEdit = participant
                            navigateTo(ScreenRoutes.PARTICIPANT_ADD)
                        },
                        onEditGroupSelected = { group ->
                            pendingCustomGroupToEdit = group
                            navigateTo(ScreenRoutes.CUSTOM_GROUP_ADD)
                        },
                        onEnterEditMode = {
                            isParticipantsEditable = true
                        },
                        onExitEditMode = {
                            isParticipantsEditable = false
                        }
                    )

                    // Success toast: slides up from the bottom after add/edit
                    AnimatedVisibility(
                        visible = groupSuccessMessage != null,
                        modifier = Modifier.align(Alignment.BottomCenter),
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .navigationBarsPadding()
                                .padding(bottom = 24.dp)
                                .background(
                                    color = Color(0xFF1C1C1E),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = groupSuccessMessage ?: "",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            ScreenRoutes.PARTICIPANT_ADD -> {
                AddParticipantScreen(
                    initialParticipant = pendingParticipantToEdit,
                    onClose = {
                        pendingParticipantToEdit = null
                        isParticipantsEditable = false
                        navigateBack()
                    },
                    onContinue = {
                        pendingParticipantToEdit = null
                        isParticipantsEditable = false
                        navigateBack()
                    }
                )
            }
            ScreenRoutes.CUSTOM_GROUP_ADD -> {
                AddCustomGroupScreen(
                    initialGroup = pendingCustomGroupToEdit,
                    onClose = {
                        pendingCustomGroupToEdit = null
                        isParticipantsEditable = false
                        navigateBack()
                    },
                    onContinue = { message ->
                        pendingCustomGroupToEdit = null
                        isParticipantsEditable = false
                        groupSuccessMessage = message
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

