package com.swifttechnology.bookingsystem.features.report.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.navigation.ScreenRoutes
import com.swifttechnology.bookingsystem.shared.components.ComingSoonContent
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ReportScreen(
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.logout()
                onLogout()
            }
        },
        onEditClick = { onNavigate(ScreenRoutes.meetingRooms(editable = true)) }
    ) {
        ComingSoonContent(title = uiState.selectedItem.label)
    }
}

