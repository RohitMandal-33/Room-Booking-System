package com.swifttechnology.bookingsystem.features.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.user.data.dtos.UserDetailsDTO
import com.swifttechnology.bookingsystem.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val currentUser: UserDetailsDTO? = null,
    val isLoadingUser: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }
            val result = userRepository.getCurrentUser()
            _uiState.update { state ->
                state.copy(
                    isLoadingUser = false,
                    currentUser = result.getOrNull() ?: state.currentUser
                )
            }
        }
    }
}
