package com.swifttechnology.bookingsystem.features.main.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.swifttechnology.bookingsystem.core.model.RoomStatus
import com.swifttechnology.bookingsystem.shared.layout.BottomSheetView
import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant
import com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import com.swifttechnology.bookingsystem.features.booking.presentation.ExternalMember
import com.swifttechnology.bookingsystem.core.utils.DateTimeUtils
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun parseBookedDateToLocalDate(bookedDate: String): LocalDate? {
    if (bookedDate.isBlank()) return null

    val formatters = listOf(
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()),
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()),
        DateTimeFormatter.ISO_LOCAL_DATE
    )

    return formatters.firstNotNullOfOrNull { formatter ->
        runCatching { LocalDate.parse(bookedDate, formatter) }.getOrNull()
    }
}

data class PendingBookingDetails(
    val bookingId: Long? = null,
    val roomName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val meetingTitle: String = "",
    val meetingType: String = "",
    val meetingTypeId: Long? = null,
    val description: String = "",
    val internalParticipantIds: List<Long> = emptyList(),
    val externalMembers: List<ExternalMember> = emptyList(),
    val recurrenceId: String? = null,
    val isRecurring: Boolean = false,
    val recurrenceType: String? = null,
    val recurrenceEndDate: String? = null,
    val updateScope: String = "ASK",
    val roomId: Long? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    startRoute: String = ScreenRoutes.DASHBOARD,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    fun isTrueRecurringSeries(recurrenceId: String?, recurrenceType: String?): Boolean {
        return !recurrenceId.isNullOrBlank() &&
            !recurrenceType.isNullOrBlank() &&
            recurrenceType.uppercase() != "NONE"
    }

    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser = mainUiState.currentUser
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
    var todayTrigger by rememberSaveable { mutableIntStateOf(0) }
    var isAnnouncementsEditMode by rememberSaveable { mutableStateOf(false) }
    var pendingRoomName by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingBookingDetails by remember { mutableStateOf<PendingBookingDetails?>(null) }
    var pendingParticipantToEdit by remember { mutableStateOf<Participant?>(null) }
    var pendingCustomGroupToEdit by remember { mutableStateOf<CustomGroup?>(null) }
    var pendingTimeOnlyUpdate by remember { mutableStateOf<Triple<String, String, String>?>(null) }
    // Maintenance dialog: holds the room name when a disabled room is tapped
    var maintenanceRoomName by remember { mutableStateOf<String?>(null) }
    // Success toast shown on various screens after actions
    var globalSuccessMessage by remember { mutableStateOf<String?>(null) }
    var pendingCalendarDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(globalSuccessMessage) {
        if (globalSuccessMessage != null) {
            kotlinx.coroutines.delay(2500)
            globalSuccessMessage = null
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

    // Under-Maintenance dialog for disabled rooms
    maintenanceRoomName?.let { roomName ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { maintenanceRoomName = null },
            title = { androidx.compose.material3.Text("Room Under Maintenance") },
            text = {
                androidx.compose.material3.Text(
                    "\"$roomName\" is currently under maintenance and unavailable for booking. " +
                    "Please try again later or choose a different room."
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { maintenanceRoomName = null }) {
                    androidx.compose.material3.Text("Got It")
                }
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
        userName = currentUser?.let { "${it.firstname} ${it.lastname}".trim() } ?: "",
        userEmail = currentUser?.email ?: "",
        userRole = currentUser?.position ?: "",
        onSidebarItemSelected = { item ->
            navigationStack = listOf(item.route)
            searchQuery = ""
            pendingRoomName = null
            pendingBookingDetails = null
            pendingTimeOnlyUpdate = null
            isMeetingRoomsEditable = false
            isParticipantsEditable = false
            isAnnouncementsEditMode = false
        },
        onLogout = onLogout,
        showEditIcon = currentRoute == ScreenRoutes.MEETING_ROOMS ||
            currentRoute == ScreenRoutes.PARTICIPANTS ||
            currentRoute == ScreenRoutes.ANNOUNCEMENTS,
        isEditMode = (currentRoute == ScreenRoutes.MEETING_ROOMS && isMeetingRoomsEditable) ||
            (currentRoute == ScreenRoutes.PARTICIPANTS && isParticipantsEditable) ||
            (currentRoute == ScreenRoutes.ANNOUNCEMENTS && isAnnouncementsEditMode),
        showTopBar = currentRoute != ScreenRoutes.ROOM_CALENDAR &&
            currentRoute != ScreenRoutes.PARTICIPANT_ADD &&
            currentRoute != ScreenRoutes.CUSTOM_GROUP_ADD &&
            currentRoute != ScreenRoutes.DEPARTMENT_ADD,
        showTodayIcon = currentRoute == ScreenRoutes.CALENDAR,
        onTodayClick = { todayTrigger++ },
        onEditClick = {
            if (currentRoute == ScreenRoutes.MEETING_ROOMS) {
                isMeetingRoomsEditable = !isMeetingRoomsEditable
            } else if (currentRoute == ScreenRoutes.PARTICIPANTS) {
                isParticipantsEditable = !isParticipantsEditable
            } else if (currentRoute == ScreenRoutes.ANNOUNCEMENTS) {
                isAnnouncementsEditMode = !isAnnouncementsEditMode
            }
        },
        onBackClick = if (navigationStack.size > 1) navigateBack else null
    ) {
        when (currentRoute) {
            ScreenRoutes.DASHBOARD -> {
                DashboardScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo,
                    onEditMeeting = { meeting, scope ->
                        val apiDate = meeting.date ?: meeting.startDate
                        val displayDate = runCatching {
                            apiDate?.let { LocalDate.parse(it) }
                                ?.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
                        }.getOrNull().orEmpty()

                        val startDisplay = runCatching {
                            DateTimeUtils.parseLocalTime(meeting.startTime)?.format(
                                DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
                            )
                        }.getOrNull().orEmpty()

                        val endDisplay = runCatching {
                            DateTimeUtils.parseLocalTime(meeting.endTime)?.format(
                                DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
                            )
                        }.getOrNull().orEmpty()

                        pendingRoomName = meeting.room?.roomName ?: meeting.roomName
                        pendingBookingDetails = PendingBookingDetails(
                            bookingId              = meeting.id ?: meeting.meetingId,
                            roomName               = meeting.room?.roomName ?: meeting.roomName ?: "",
                            date                   = displayDate,
                            startTime              = startDisplay,
                            endTime                = endDisplay,
                            meetingTitle           = meeting.meetingTitle ?: "",
                            meetingType            = meeting.meetingType ?: "",
                            meetingTypeId          = meeting.meetingTypeId,
                            description            = meeting.description ?: "",
                            internalParticipantIds = meeting.internalParticipant?.mapNotNull { it.id } ?: emptyList(),
                            externalMembers        = meeting.externalParticipant?.map { ExternalMember(it.name, it.email) } ?: emptyList(),
                            recurrenceId           = meeting.recurrenceId,
                            isRecurring            = isTrueRecurringSeries(meeting.recurrenceId, meeting.recurrenceType),
                            recurrenceType         = meeting.recurrenceType,
                            updateScope            = scope,
                            roomId                 = meeting.roomId ?: meeting.room?.id
                        )
                        navigateTo(ScreenRoutes.BOOK_ROOM)
                    }
                )
            }
            ScreenRoutes.CALENDAR -> {
                CalendarScreen(
                    searchQuery = searchQuery,
                    onNavigate = navigateTo,
                    todayTrigger = todayTrigger,
                    initialDate = pendingCalendarDate,
                    onDateConsumed = { pendingCalendarDate = null },
                    onProceedWithDetails = { room, date, start, end ->
                        pendingRoomName = room
                        pendingBookingDetails = PendingBookingDetails(
                            roomName = room,
                            date = date,
                            startTime = start,
                            endTime = end
                        )
                        navigateTo(ScreenRoutes.BOOK_ROOM)
                    },
                    onEditMeeting = { event, scope ->
                        // Pre-fill room name and date/time from the event
                        pendingRoomName = event.meetingRoom.takeIf { it.isNotBlank() }
                        val dateFmt = event.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
                        val startFmt = event.startTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
                        val endFmt   = event.endTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))
                        pendingBookingDetails = PendingBookingDetails(
                            // Some calendar payloads provide only `id` for an occurrence.
                            // Fallback to it so single-occurrence updates always carry bookingId.
                            bookingId              = event.meetingId ?: event.id.toLong(),
                            roomName               = event.meetingRoom,
                            date                   = dateFmt,
                            startTime              = startFmt,
                            endTime                = endFmt,
                            meetingTitle           = event.title,
                            meetingType            = event.meetingType,
                            meetingTypeId          = event.meetingTypeId,
                            description            = event.description,
                            internalParticipantIds = event.internalParticipants.mapNotNull { it.id?.toLong() },
                            externalMembers        = event.externalParticipants.map { ExternalMember(it.name ?: "", it.email ?: "") },
                            recurrenceId           = event.recurrenceId,
                            isRecurring            = isTrueRecurringSeries(event.recurrenceId, event.recurrenceType),
                            recurrenceType         = event.recurrenceType,
                            updateScope            = scope,
                            roomId                 = event.roomId
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
                        val isDisabled = room.status == RoomStatus.INACTIVE ||
                            room.status == RoomStatus.DISABLED
                        if (isDisabled) {
                            maintenanceRoomName = room.name
                        } else {
                            // Start a clean booking flow from room cards.
                            // Without this reset, stale edit payload (bookingId, recurrence info)
                            // can leak from previous edit sessions and force update APIs.
                            pendingBookingDetails = null
                            pendingTimeOnlyUpdate = null
                            pendingRoomName = room.name
                            showRoomCalendarBottomSheet = true
                        }
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
                    onTimeUpdate = { date, start, end ->
                        pendingTimeOnlyUpdate = null
                    },
                    pendingTimeUpdate = pendingTimeOnlyUpdate,
                    onNavigateToCalendar = { currentDetails ->
                        pendingBookingDetails = currentDetails
                        showRoomCalendarBottomSheet = true
                    },
                    onNavigate = navigateTo,
                    onSuccess = { bookedDate ->
                        val isUpdate = pendingBookingDetails?.bookingId != null
                        globalSuccessMessage = if (isUpdate) {
                            "Room updated successfully!"
                        } else {
                            "Room booked successfully!"
                        }

                        // Always land in Calendar Day view after save; if parsing fails, default to today.
                        pendingCalendarDate = parseBookedDateToLocalDate(bookedDate) ?: LocalDate.now()
                        pendingBookingDetails = null
                        pendingRoomName = null
                        pendingTimeOnlyUpdate = null
                        showRoomCalendarBottomSheet = false
                        // Replace BOOK_ROOM with CALENDAR in the stack to avoid back-navigating to an empty form
                        navigationStack = navigationStack.dropLast(1) + ScreenRoutes.CALENDAR
                    },
                    onNavigateToEdit = {
                        navigationStack = listOf(ScreenRoutes.MEETING_ROOMS)
                        isMeetingRoomsEditable = true
                    }
                )
            }
            ScreenRoutes.ANNOUNCEMENTS -> {
                AnnouncementsScreen(
                    searchQuery = searchQuery,
                    isEditable = isAnnouncementsEditMode,
                    onNavigate  = navigateTo,
                    onEnterEditMode = { isAnnouncementsEditMode = true },
                    onExitEditMode = { isAnnouncementsEditMode = false }
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

                    // Success toast: slides up from the bottom after actions
                    AnimatedVisibility(
                        visible = globalSuccessMessage != null,
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
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = globalSuccessMessage ?: "",
                                color = MaterialTheme.colorScheme.inverseOnSurface,
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
                    onContinue = { message ->
                        pendingParticipantToEdit = null
                        isParticipantsEditable = false
                        globalSuccessMessage = message
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
                        globalSuccessMessage = message
                        navigateBack()
                    }
                )
            }
            ScreenRoutes.DEPARTMENT_ADD -> {
                com.swifttechnology.bookingsystem.features.participants.presentation.components.AddDepartmentScreen(
                    onClose = {
                        isParticipantsEditable = false
                        navigateBack()
                    },
                    onContinue = { message ->
                        isParticipantsEditable = false
                        globalSuccessMessage = message
                        navigateBack()
                    }
                )
            }
            ScreenRoutes.SETTINGS -> {
                SettingsScreen(
                    searchQuery = searchQuery,
                    currentUser = currentUser,
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
                    if (currentRoute == ScreenRoutes.BOOK_ROOM) {
                        pendingTimeOnlyUpdate = Triple(details.date, details.startTime, details.endTime)
                    } else {
                        pendingBookingDetails = details
                        navigateTo(ScreenRoutes.BOOK_ROOM)
                    }
                    showRoomCalendarBottomSheet = false
                }
            )
        }
    }
}

