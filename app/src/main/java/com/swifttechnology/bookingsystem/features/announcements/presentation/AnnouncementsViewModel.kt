package com.swifttechnology.bookingsystem.features.announcements.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementRequestDTO
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import com.swifttechnology.bookingsystem.features.announcements.domain.repository.AnnouncementRepository
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementsViewModel @Inject constructor(
    private val repository: AnnouncementRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnouncementsUiState())
    val uiState: StateFlow<AnnouncementsUiState> = _uiState.asStateFlow()

    init {
        loadAnnouncements()
    }

    //     Data loading                                                           

    fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val allResult     = repository.getAllAnnouncements()
            val pinnedResult  = repository.getPinnedAnnouncements()
            _uiState.update { state ->
                state.copy(
                    isLoading           = false,
                    allAnnouncements    = allResult.getOrDefault(emptyList()),
                    pinnedAnnouncements = pinnedResult.getOrDefault(emptyList()),
                    errorMessage        = allResult.exceptionOrNull()?.message
                )
            }
        }
    }

    //     Tab selection                                                          

    fun onTabSelected(tab: AnnouncementTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    //     Add / Edit sheet                                                       

    fun openAddSheet() {
        _uiState.update { it.copy(showAddEditSheet = true, editingAnnouncement = null) }
    }

    fun openEditSheet(announcement: Announcement) {
        _uiState.update { it.copy(showAddEditSheet = true, editingAnnouncement = announcement) }
    }

    fun dismissSheet() {
        _uiState.update { it.copy(showAddEditSheet = false, editingAnnouncement = null) }
    }

    //     CRUD operations                                                       

    fun saveAnnouncement(request: AnnouncementRequestDTO) {
        viewModelScope.launch {
            val editing = _uiState.value.editingAnnouncement
            val result = if (editing != null) {
                repository.updateAnnouncement(editing.id, request)
            } else {
                repository.addAnnouncement(request)
            }
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        showAddEditSheet    = false,
                        editingAnnouncement = null,
                        successMessage      = if (editing != null) "Announcement updated" else "Announcement added"
                    )
                }
                loadAnnouncements()
            } else {
                _uiState.update { it.copy(operationError = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun deleteSelected(ids: Set<Long>) {
        viewModelScope.launch {
            val list = ids.toList()
            val result = repository.deleteBulkAnnouncements(list)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(successMessage = "Deleted ${list.size} announcement(s)")
                }
            } else {
                _uiState.update {
                    it.copy(operationError = result.exceptionOrNull()?.message)
                }
            }
            loadAnnouncements()
        }
    }

    fun togglePin(id: Long) {
        viewModelScope.launch {
            repository.changePinStatus(id)
            loadAnnouncements()
        }
    }

    //     Search                                                                 

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    //     Sidebar / misc                                                         

    fun onSidebarItemSelected(item: SidebarItem) {
        _uiState.update { current ->
            current.copy(
                sidebarItems = current.sidebarItems.map { it.copy(isActive = it.route == item.route) },
                selectedItem = item.copy(isActive = true)
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, operationError = null) }
    }

    suspend fun logout() {
        tokenStorage.clear()
    }
}
