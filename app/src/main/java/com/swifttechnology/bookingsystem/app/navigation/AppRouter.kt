package com.swifttechnology.bookingsystem.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.swifttechnology.bookingsystem.navigation.AppNavGraph
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes

@Composable
fun AppRouter(
    navController: NavHostController = rememberNavController()
) {
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
