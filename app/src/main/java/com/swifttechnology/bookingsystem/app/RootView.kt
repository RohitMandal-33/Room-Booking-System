package com.swifttechnology.bookingsystem.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swifttechnology.bookingsystem.app.navigation.AppRouter
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RootView() {
    MeetingRoomBookingTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
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

