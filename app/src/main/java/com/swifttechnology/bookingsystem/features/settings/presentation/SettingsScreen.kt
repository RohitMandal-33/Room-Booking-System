package com.swifttechnology.bookingsystem.features.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.shared.components.ComingSoonContent
import com.swifttechnology.bookingsystem.shared.layout.MainScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
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
        }
    ) {
        ComingSoonContent(title = uiState.selectedItem.label)
    }
}

