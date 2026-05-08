package com.swifttechnology.bookingsystem.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swifttechnology.bookingsystem.features.auth.presentation.login.LoginScreen
import com.swifttechnology.bookingsystem.features.main.presentation.MainAppScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomComponents.EditRoomCardScreen
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.MeetingRoomComponents.AddRoomScreen
import com.swifttechnology.bookingsystem.features.auth.presentation.login.ForgotPasswordScreen
import com.swifttechnology.bookingsystem.features.auth.presentation.login.VerifyOtpScreen
import com.swifttechnology.bookingsystem.features.auth.presentation.login.ResetPasswordScreen
import com.swifttechnology.bookingsystem.features.auth.presentation.login.ForgotPasswordViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.remember

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
                },
                onForgotPasswordClick = {
                    navController.navigate(ScreenRoutes.FORGOT_PASSWORD)
                }
            )
        }

        composable(ScreenRoutes.FORGOT_PASSWORD) {
            val viewModel: ForgotPasswordViewModel = hiltViewModel()
            ForgotPasswordScreen(
                onNavigateToOtp = { email ->
                    navController.navigate(ScreenRoutes.VERIFY_OTP)
                },
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(ScreenRoutes.VERIFY_OTP) {
            // Get the view model from the forgot password back stack entry to share state
            val parentEntry = remember(it) {
                navController.getBackStackEntry(ScreenRoutes.FORGOT_PASSWORD)
            }
            val viewModel: ForgotPasswordViewModel = hiltViewModel(parentEntry)
            VerifyOtpScreen(
                onNavigateToResetPassword = { referenceId ->
                    navController.navigate(ScreenRoutes.RESET_PASSWORD)
                },
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(ScreenRoutes.RESET_PASSWORD) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(ScreenRoutes.FORGOT_PASSWORD)
            }
            val viewModel: ForgotPasswordViewModel = hiltViewModel(parentEntry)
            ResetPasswordScreen(
                onNavigateToLogin = {
                    navController.navigate(ScreenRoutes.LOGIN) {
                        popUpTo(ScreenRoutes.FORGOT_PASSWORD) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                viewModel = viewModel
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
            val roomName = Uri.decode(
                entry.arguments?.getString(ScreenRoutes.MEETING_ROOM_NAME).orEmpty()
            )
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

