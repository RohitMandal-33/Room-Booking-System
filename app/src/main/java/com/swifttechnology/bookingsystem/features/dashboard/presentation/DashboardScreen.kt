package com.swifttechnology.bookingsystem.features.dashboard.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.AnnouncementSection
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.CalendarPreviewSection
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.MeetingCardsSection
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent.StatTilesSection
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes



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

    var viewingAnnouncement by remember { 
        androidx.compose.runtime.mutableStateOf<com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement?>(null) 
    }




    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshAll()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
            announcements = uiState.announcements,
            isLoading     = uiState.isLoadingAnnouncements,
            onView        = { announcement -> viewingAnnouncement = announcement },
            onSeeAllClick = { onNavigate(ScreenRoutes.ANNOUNCEMENTS) }
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        // 3. Calendar preview with inline range picker
        CalendarPreviewSection(
            events             = uiState.calendarMeetings,
            onMonthChange      = { month -> viewModel.fetchCalendarEvents(month) },
            onOpenFullCalendar = { onNavigate(ScreenRoutes.CALENDAR) }
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        // 4. Upcoming meeting cards
        MeetingCardsSection(
            meetings  = uiState.upcomingMeetings,
            isLoading = uiState.isLoadingMeetings,
            error     = uiState.error
        )

        Spacer(modifier = Modifier.height(Spacing.lg))
    }

    viewingAnnouncement?.let { announcement ->
        com.swifttechnology.bookingsystem.features.announcements.presentation.components.AnnouncementDetailDialog(
            announcement = announcement,
            onDismiss = { viewingAnnouncement = null }
        )
    }
}
