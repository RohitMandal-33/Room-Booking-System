package com.swifttechnology.bookingsystem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swifttechnology.bookingsystem.features.auth.presentation.login.LoginScreen
import com.swifttechnology.bookingsystem.features.main.presentation.MainAppScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomComponets.EditRoomCardScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomComponets.AddRoomScreen

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
                    navController.navigate(ScreenRoutes.MAIN_APP) {
                        popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.MAIN_APP) {
            MainAppScreen(
                navController = navController,
                onLogout = onLogout
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
                onNavigate = { navController.navigate(ScreenRoutes.MAIN_APP) }
            )
        }

        composable(ScreenRoutes.MEETING_ROOM_ADD) {
            AddRoomScreen(
                onBack = { navController.popBackStack() },
                onLogout = onLogout,
                onNavigate = { navController.navigate(ScreenRoutes.MAIN_APP) }
            )
        }
    }
}

