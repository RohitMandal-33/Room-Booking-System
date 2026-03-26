package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.participants.domain.repository.ParticipantRepository
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val participantRepository: ParticipantRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParticipantsUiState())
    val uiState: StateFlow<ParticipantsUiState> = _uiState.asStateFlow()

    init {
        // Observe search query to fetch participants automatically
        _uiState
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(300L)
            .flatMapLatest { query ->
                participantRepository.searchParticipants(query)
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                    }
            }
            .onEach { participants ->
                _uiState.update { it.copy(participants = participants, isLoading = false, error = null) }
            }
            .launchIn(viewModelScope)
    }

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

    fun onTabSelected(tab: ParticipantsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun onAddParticipantClicked() {
        // Handle FAB click
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
