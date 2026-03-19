package com.swifttechnology.bookingsystem.app.navigation

import androidx.navigation.NavHostController

/**
 * Centralized navigation actions. Use these in [AppRouter] (or from ViewModels with a
 * NavController reference) to keep routing logic and back-stack behavior in one place.
 */
fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.navigateToDashboard(fromLogin: Boolean = false) {
    navigate(AppRoute.Dashboard.route) {
        if (fromLogin) {
            popUpTo(AppRoute.Login.route) { inclusive = true }
        }
    }
}

fun NavHostController.navigateToRoomBooking() {
    navigate(AppRoute.RoomBooking.route)
}

fun NavHostController.navigateToLogin(clearBackStack: Boolean = true) {
    navigate(AppRoute.Login.route) {
        if (clearBackStack) {
            popUpTo(graph.id) { inclusive = true }
        }
    }
}

fun NavHostController.navigateBackToDashboard() {
    navigate(AppRoute.Dashboard.route) {
        popUpTo(AppRoute.RoomBooking.route) { inclusive = true }
    }
}

/** Navigate to Login and clear the entire back stack */
fun NavHostController.navigateBackToLogin() {
    navigate(AppRoute.Login.route) {
        popUpTo(graph.id) { inclusive = true }
    }
}
