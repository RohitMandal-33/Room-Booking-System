package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.auth.domain.repository.AuthRepository
import com.swifttechnology.bookingsystem.features.participants.domain.repository.CustomGroupRepository
import com.swifttechnology.bookingsystem.features.participants.domain.repository.ParticipantRepository
import com.swifttechnology.bookingsystem.features.department.domain.repository.DepartmentRepository
import com.swifttechnology.bookingsystem.features.user.data.dtos.UserDetailsDTO
import com.swifttechnology.bookingsystem.features.user.domain.repository.UserRepository
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val participantRepository: ParticipantRepository,
    private val customGroupRepository: CustomGroupRepository,
    private val departmentRepository: DepartmentRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParticipantsUiState())
    val uiState: StateFlow<ParticipantsUiState> = _uiState.asStateFlow()

    init {
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
        fetchDepartments()
    }

    private fun fetchCustomGroups() {
        customGroupRepository.getCustomGroups()
            .onEach { groups ->
                _uiState.update { state ->
                    val newNames = groups.fold(mutableMapOf<Long, String>()) { acc, group ->
                        acc.putAll(group.preResolvedMemberNames)
                        acc
                    }
                    state.copy(
                        customGroups = groups,
                        resolvedUserNames = state.resolvedUserNames + newNames
                    )
                }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    private fun fetchDepartments() {
        viewModelScope.launch {
            departmentRepository.getDepartments()
                .onSuccess { depts ->
                    _uiState.update { it.copy(departments = depts) }
                }
                .onFailure { err ->
                    // Optionally handle error
                }
        }
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
        _uiState.update { it.copy(selectedTab = tab, expandedCustomGroupId = null) }
    }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
    }

    fun onCustomGroupMembersClick(groupId: Long) {
        val current = _uiState.value.expandedCustomGroupId
        if (current == groupId) {
            _uiState.update { it.copy(expandedCustomGroupId = null) }
            return
        }
        _uiState.update { it.copy(expandedCustomGroupId = groupId) }
        val group = _uiState.value.customGroups.find { it.id == groupId } ?: return
        val missingIds = group.memberIds.filter { id ->
            _uiState.value.participants.none { it.id == id } &&
                !_uiState.value.resolvedUserNames.containsKey(id)
        }
        if (missingIds.isEmpty()) return
        viewModelScope.launch {
            participantRepository.getParticipantsByIds(missingIds).onSuccess { participants ->
                val updates = participants.associate { it.id to it.name }
                if (updates.isNotEmpty()) {
                    _uiState.update { s ->
                        s.copy(resolvedUserNames = s.resolvedUserNames + updates)
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun deleteCustomGroups(ids: Set<Long>) {
        viewModelScope.launch {
            for (id in ids) {
                customGroupRepository.deleteCustomGroup(id)
            }
            refresh()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val query = _uiState.value.searchQuery
            participantRepository.searchParticipants(query)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
                .firstOrNull()
                ?.let { participants ->
                    _uiState.update { it.copy(participants = participants, isLoading = false, error = null) }
                }

            customGroupRepository.getCustomGroups()
                .catch { }
                .firstOrNull()
                ?.let { groups ->
                    _uiState.update { it.copy(customGroups = groups) }
                }

            fetchDepartments()
        }
    }
}

private fun UserDetailsDTO.displayName(): String {
    val n = listOfNotNull(firstname, lastname).joinToString(" ").trim()
    return if (n.isNotBlank()) n else email
}
