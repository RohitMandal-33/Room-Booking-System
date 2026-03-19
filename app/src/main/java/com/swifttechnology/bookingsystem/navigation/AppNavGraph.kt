package com.swifttechnology.bookingsystem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swifttechnology.bookingsystem.app.navigation.navigateTopLevel
import com.swifttechnology.bookingsystem.features.announcements.presentation.AnnouncementsScreen
import com.swifttechnology.bookingsystem.features.auth.presentation.login.LoginScreen
import com.swifttechnology.bookingsystem.features.booking.presentation.BookRoomScreen
import com.swifttechnology.bookingsystem.features.calendar.presentation.CalendarScreen
import com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomsScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomComponets.EditRoomCardScreen
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
                    navController.navigateTopLevel(route)
                }
            )
        }

        composable(ScreenRoutes.CALENDAR) {
            CalendarScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigateTopLevel(it) }
            )
        }

        composable(ScreenRoutes.BOOK_ROOM) {
            BookRoomScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigateTopLevel(it) }
            )
        }

        composable(
            route = "${ScreenRoutes.MEETING_ROOMS}?${ScreenRoutes.MEETING_ROOMS_EDITABLE_QUERY}={${ScreenRoutes.MEETING_ROOMS_EDITABLE_QUERY}}",
            arguments = listOf(
                navArgument(ScreenRoutes.MEETING_ROOMS_EDITABLE_QUERY) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { entry ->
            MeetingRoomsScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigateTopLevel(it) },
                onOpenRoomEdit = { roomName ->
                    navController.navigate(ScreenRoutes.meetingRoomEdit(roomName))
                },
                isEditable = entry.arguments?.getBoolean(ScreenRoutes.MEETING_ROOMS_EDITABLE_QUERY) ?: false
            )
        }

        composable(
            route = "${ScreenRoutes.MEETING_ROOM_EDIT}/{${ScreenRoutes.MEETING_ROOM_NAME}}",
            arguments = listOf(
                navArgument(ScreenRoutes.MEETING_ROOM_NAME) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val roomName = entry.arguments?.getString(ScreenRoutes.MEETING_ROOM_NAME).orEmpty()
            EditRoomCardScreen(
                roomName = roomName,
                onBack = { navController.popBackStack() },
                onLogout = onLogout,
                onNavigate = { navController.navigateTopLevel(it) }
            )
        }

        composable(ScreenRoutes.ANNOUNCEMENTS) {
            AnnouncementsScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigateTopLevel(it) }
            )
        }

        composable(ScreenRoutes.REPORT) {
            ReportScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigateTopLevel(it) }
            )
        }

        composable(ScreenRoutes.PARTICIPANTS) {
            ParticipantsScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigateTopLevel(it) }
            )
        }

        composable(ScreenRoutes.SETTINGS) {
            SettingsScreen(
                onLogout = onLogout,
                onNavigate = { navController.navigateTopLevel(it) }
            )
        }
    }
}

