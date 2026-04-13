package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.booking.presentation.InternalMember
import com.swifttechnology.bookingsystem.features.booking.presentation.RoomBookingFormState
import com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup
import com.swifttechnology.bookingsystem.features.participants.domain.repository.CustomGroupRepository
import com.swifttechnology.bookingsystem.features.participants.domain.repository.ParticipantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddCustomGroupUiState(
    val formState: RoomBookingFormState = RoomBookingFormState(),
    val editGroupId: Long? = null,
    val participantSearchQuery: String = "",
    val availableParticipants: List<InternalMember> = emptyList(),
    val isSearchingParticipants: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddCustomGroupViewModel @Inject constructor(
    private val participantRepository: ParticipantRepository,
    private val customGroupRepository: CustomGroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCustomGroupUiState())
    val uiState: StateFlow<AddCustomGroupUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var pendingMemberIds: List<Long>? = null

    init {
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearchingParticipants = true) }
            participantRepository.searchParticipants("").collect { participants ->
                val internalMembers = participants.map { p ->
                    InternalMember(
                        id = p.id,
                        name = p.name,
                        email = p.email,
                        department = p.department
                    )
                }
                _uiState.update {
                    it.copy(availableParticipants = internalMembers, isSearchingParticipants = false)
                }
                tryResolvePendingMembers()
            }
        }
    }

    fun initForEdit(group: CustomGroup) {
        if (_uiState.value.editGroupId != null) return
        _uiState.update {
            it.copy(
                editGroupId = group.id,
                formState = it.formState.copy(
                    meetingTitle = group.name,
                    description = group.description
                )
            )
        }
        pendingMemberIds = group.memberIds
        tryResolvePendingMembers()
    }

    private fun tryResolvePendingMembers() {
        val ids = pendingMemberIds ?: return
        val available = _uiState.value.availableParticipants
        
        // Split IDs into those we already have and those we need to fetch
        val knownMembers = available.filter { it.id in ids }
        val missingIds = ids.filter { id -> available.none { it.id == id } }
        
        if (missingIds.isEmpty()) {
            pendingMemberIds = null
            _uiState.update {
                it.copy(formState = it.formState.copy(participants = knownMembers))
            }
        } else {
            // Fetch missing members
            viewModelScope.launch {
                participantRepository.getParticipantsByIds(missingIds).onSuccess { participants ->
                    val missingMembers = participants.map { p ->
                        InternalMember(id = p.id, name = p.name, email = p.email, department = p.department)
                    }
                    val allResolved = (knownMembers + missingMembers).distinctBy { it.id }
                    pendingMemberIds = null
                    _uiState.update {
                        it.copy(formState = it.formState.copy(participants = allResolved))
                    }
                }
            }
        }
    }

    fun onFormStateChanged(formState: RoomBookingFormState) {
        _uiState.update { it.copy(formState = formState) }
    }

    fun onParticipantSearchQueryChanged(query: String) {
        _uiState.update { it.copy(participantSearchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) delay(300)
            _uiState.update { it.copy(isSearchingParticipants = true) }
            participantRepository.searchParticipants(query).collect { participants ->
                val internalMembers = participants.map { p ->
                    InternalMember(
                        id = p.id,
                        name = p.name,
                        email = p.email,
                        department = p.department
                    )
                }
                _uiState.update {
                    it.copy(availableParticipants = internalMembers, isSearchingParticipants = false)
                }
            }
        }
    }

    fun submit() {
        val editId = _uiState.value.editGroupId
        if (editId != null) {
            submitUpdateCustomGroup(editId)
        } else {
            submitCreateCustomGroup()
        }
    }

    fun submitCreateCustomGroup() {
        val fs = _uiState.value.formState
        val name = fs.meetingTitle.trim()
        val desc = fs.description.trim()
        val memberIds = fs.participants.map { it.id }
        if (name.isBlank() || memberIds.isEmpty()) {
            _uiState.update {
                it.copy(error = "Please enter a group name and at least one internal member.")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = customGroupRepository.createCustomGroup(
                name = name,
                description = desc.takeIf { it.isNotEmpty() },
                memberIds = memberIds
            )
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to create group")
                }
            }
        }
    }

    private fun submitUpdateCustomGroup(groupId: Long) {
        val fs = _uiState.value.formState
        val name = fs.meetingTitle.trim()
        val desc = fs.description.trim()
        val memberIds = fs.participants.map { it.id }
        if (name.isBlank() || memberIds.isEmpty()) {
            _uiState.update {
                it.copy(error = "Please enter a group name and at least one internal member.")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            customGroupRepository.updateCustomGroup(
                id = groupId,
                name = name,
                description = desc.takeIf { it.isNotEmpty() },
                memberIds = memberIds
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to update group")
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { current ->
            AddCustomGroupUiState(
                availableParticipants = current.availableParticipants,
                isSuccess = false,
                isLoading = false,
                error = null
            )
        }
    }
}
