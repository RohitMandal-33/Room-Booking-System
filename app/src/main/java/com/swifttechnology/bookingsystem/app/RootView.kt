package com.swifttechnology.bookingsystem.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.swifttechnology.bookingsystem.app.navigation.AppRouter
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme
import com.swifttechnology.bookingsystem.core.designsystem.SystemBarAppearance
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RootView() {
    val themeVm: AppThemeViewModel = hiltViewModel()
    val themeMode by themeVm.themeMode.collectAsStateWithLifecycle()

    MeetingRoomBookingTheme(themeMode = themeMode) {
        val isDark = when (themeMode) {
            com.swifttechnology.bookingsystem.core.designsystem.ThemeMode.SYSTEM ->
                androidx.compose.foundation.isSystemInDarkTheme()
            com.swifttechnology.bookingsystem.core.designsystem.ThemeMode.DARK -> true
            com.swifttechnology.bookingsystem.core.designsystem.ThemeMode.LIGHT -> false
        }
        SystemBarAppearance(isDarkTheme = isDark)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.systemBarsPadding()) {
                AppRouter()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RootViewPreview() {
    RootView()
}

