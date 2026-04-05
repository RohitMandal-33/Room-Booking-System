package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.participants.domain.repository.ParticipantRepository
import com.swifttechnology.bookingsystem.features.participants.domain.repository.CustomGroupRepository
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
    private val participantRepository: ParticipantRepository,
    private val customGroupRepository: CustomGroupRepository
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
                _uiState.update { it.copy(isLoading = true, error = null) }
                participantRepository.searchParticipants(query)
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                    }
            }
            .onEach { participants ->
                _uiState.update { it.copy(participants = participants, isLoading = false, error = null) }
            }
            .launchIn(viewModelScope)

        fetchCustomGroups()
    }

    private fun fetchCustomGroups() {
        customGroupRepository.getCustomGroups()
            .onEach { groups ->
                _uiState.update { it.copy(customGroups = groups) }
            }
            .catch { e ->
                // Handle gently inside ui state if wanted, or just ignore if it shouldn't crash the main participant view
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

    fun onAddGroupClicked() {
        _uiState.update { it.copy(showAddGroupDialog = true) }
    }

    fun dismissAddGroupDialog() {
        _uiState.update { it.copy(showAddGroupDialog = false) }
    }

    fun createCustomGroup(name: String, description: String, memberIds: List<Long>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = customGroupRepository.createCustomGroup(name, description, memberIds)
            result.onSuccess {
                dismissAddGroupDialog()
                fetchCustomGroups()
                _uiState.update { it.copy(isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to create group") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
