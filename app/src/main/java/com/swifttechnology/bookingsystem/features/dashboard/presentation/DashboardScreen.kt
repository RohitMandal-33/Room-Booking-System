package com.swifttechnology.bookingsystem.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.rememberCoroutineScope
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import kotlinx.coroutines.launch



@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    MainScaffold(
        sidebarItems = uiState.sidebarItems,
        selectedItem = uiState.selectedItem,
        searchQuery = uiState.searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSidebarItemSelected = { item ->
            viewModel.onSidebarItemSelected(item)
            onNavigate(item.route)
        },
        onLogout = {
            scope.launch {
                viewModel.logout()
                onLogout()
            }
        },
        containerColor = MaterialTheme.customColors.dashboardBg,
        onEditClick = { onNavigate(com.swifttechnology.bookingsystem.navigation.ScreenRoutes.MEETING_ROOMS) }
    ) {
        DashboardOverview()
    }
}

@Composable
private fun DashboardOverview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Today’s Summary",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.customColors.deepBlack
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewCard(title = "Total Rooms", value = "12")
            OverviewCard(title = "Booked", value = "7")
            OverviewCard(title = "Available", value = "5")
        }
    }
}

@Composable
private fun RowScope.OverviewCard(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.customColors.neutral100)
            .border(0.8.dp, MaterialTheme.customColors.neutral300, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.customColors.neutral700
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.customColors.deepBlack
        )
    }
}
