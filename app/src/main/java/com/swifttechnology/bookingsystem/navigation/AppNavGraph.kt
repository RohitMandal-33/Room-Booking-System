package com.swifttechnology.bookingsystem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.swifttechnology.bookingsystem.features.announcements.presentation.AnnouncementsScreen
import com.swifttechnology.bookingsystem.features.auth.presentation.login.LoginScreen
import com.swifttechnology.bookingsystem.features.booking.presentation.BookRoomScreen
import com.swifttechnology.bookingsystem.features.calendar.presentation.CalendarScreen
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomsScreen
import com.swifttechnology.bookingsystem.features.participants.presentation.ParticipantsScreen
import com.swifttechnology.bookingsystem.features.report.presentation.ReportScreen
import com.swifttechnology.bookingsystem.features.settings.presentation.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = ScreenRoutes.LOGIN,
    onLogout: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ScreenRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ScreenRoutes.DASHBOARD) {
                        popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.DASHBOARD) {
            DashboardScreen(
                onLogout = onLogout,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(ScreenRoutes.CALENDAR) {
            CalendarScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } }
            )
        }

        composable(ScreenRoutes.BOOK_ROOM) {
            BookRoomScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } }
            )
        }

        composable(ScreenRoutes.MEETING_ROOMS) {
            MeetingRoomsScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } }
            )
        }

        composable(ScreenRoutes.ANNOUNCEMENTS) {
            AnnouncementsScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } }
            )
        }

        composable(ScreenRoutes.REPORT) {
            ReportScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } }
            )
        }

        composable(ScreenRoutes.PARTICIPANTS) {
            ParticipantsScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } }
            )
        }

        composable(ScreenRoutes.SETTINGS) {
            SettingsScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigate(it) { launchSingleTop = true } }
            )
        }
    }
}

