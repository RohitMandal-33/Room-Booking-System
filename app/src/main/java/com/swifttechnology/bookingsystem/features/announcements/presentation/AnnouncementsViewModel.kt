package com.swifttechnology.bookingsystem.features.announcements.presentation

import androidx.lifecycle.ViewModel
import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class AnnouncementsViewModel @Inject constructor(
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnnouncementsUiState())
    val uiState: StateFlow<AnnouncementsUiState> = _uiState.asStateFlow()

    fun onSidebarItemSelected(item: SidebarItem) {
        _uiState.update { current ->
            current.copy(
                sidebarItems = current.sidebarItems.map { it.copy(isActive = it.route == item.route) },
                selectedItem = item.copy(isActive = true)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    suspend fun logout() {
        tokenStorage.clear()
    }
}

