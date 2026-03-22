package com.swifttechnology.bookingsystem.features.announcements.presentation

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

import androidx.compose.runtime.LaunchedEffect

@Composable
fun AnnouncementsScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    viewModel: AnnouncementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    ComingSoonContent(title = uiState.selectedItem.label)
}
