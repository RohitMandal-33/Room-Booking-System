package com.swifttechnology.bookingsystem.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.swifttechnology.bookingsystem.navigation.AppNavGraph
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.swifttechnology.bookingsystem.app.AppViewModel

@Composable
fun AppRouter(
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        appViewModel.forceLogoutEvent.collect {
            navController.navigate(ScreenRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    AppNavGraph(
        navController = navController,
        startDestination = ScreenRoutes.LOGIN,
        onLogout = {
            navController.navigate(ScreenRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AppRouterPreview() {
    AppRouter()
}
