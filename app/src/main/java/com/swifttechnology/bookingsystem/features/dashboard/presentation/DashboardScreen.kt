package com.swifttechnology.bookingsystem.features.dashboard.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.AnnouncementSection
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.CalendarPreviewSection
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.MeetingCardsSection
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.RoomBookingHeader
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.RoomDropdownOverlay
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.StatTilesSection
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.TotalBookingsRow
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import androidx.lifecycle.compose.collectAsStateWithLifecycle



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    // UI state
    var dropdownExpanded   by remember { mutableStateOf(false) }
    var selectedPeriod     by remember { mutableStateOf("Days") }
    var selectedDay        by remember { mutableIntStateOf(3) }
    var dateSearchQuery    by remember { mutableStateOf("") }

    // Bounds of the room-picker anchor for overlay positioning
    var roomDropdownAnchorPos  by remember { mutableStateOf<IntOffset?>(null) }
    var roomDropdownAnchorSize by remember { mutableStateOf<IntSize?>(null) }
    var rootWindowPos          by remember { mutableStateOf<IntOffset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                val pos = coords.positionInWindow()
                rootWindowPos = IntOffset(pos.x.toInt(), pos.y.toInt())
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. KPI stat tiles
            StatTilesSection()

            Spacer(modifier = Modifier.height(Spacing.sm))

            // 2. Announcement carousel
            AnnouncementSection(
                onView = { onNavigate(ScreenRoutes.ANNOUNCEMENTS) }
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // 3. Calendar Preview (new component)
            CalendarPreviewSection(
                onOpenFullCalendar = { onNavigate(ScreenRoutes.CALENDAR) }
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // 4. Room booking statistics header
//            RoomBookingHeader(
//                selectedRoom               = selectedRoom,
//                dropdownExpanded            = dropdownExpanded,
//                onDropdownToggle            = { dropdownExpanded = !dropdownExpanded },
//                onDropdownAnchorPositioned  = { pos, size ->
//                    roomDropdownAnchorPos  = pos
//                    roomDropdownAnchorSize = size
//                }
//            )

//            Spacer(modifier = Modifier.height(Spacing.sm))


            // 6. Total bookings + Book CTA
//            TotalBookingsRow(room = selectedRoom)

//            Spacer(modifier = Modifier.height(Spacing.sm))

            // 7. Upcoming meeting cards
            MeetingCardsSection(
                meetings = uiState.upcomingMeetings,
                isLoading = uiState.isLoadingMeetings,
                error = uiState.error
            )

            Spacer(modifier = Modifier.height(Spacing.lg))
        }

        // Floating room-picker dropdown overlay
//        if (dropdownExpanded) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clickable { dropdownExpanded = false }
//            ) {
//                val anchorPos  = roomDropdownAnchorPos
//                val anchorSize = roomDropdownAnchorSize
//                val rootPos    = rootWindowPos
//
//                if (anchorPos != null && anchorSize != null && rootPos != null) {
//                    val localX = anchorPos.x - rootPos.x
//                    val localY = anchorPos.y - rootPos.y + anchorSize.height
//
//                    RoomDropdownOverlay(
//                        rooms          = rooms,
//                        selectedRoom   = selectedRoom,
//                        onRoomSelected = { selectedRoom = it; dropdownExpanded = false },
//                        modifier       = Modifier.offset { IntOffset(localX, localY) }
//                    )
//                }
//            }
//        }
    }
}
