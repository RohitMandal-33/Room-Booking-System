package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.department.domain.repository.DepartmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddDepartmentUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AddDepartmentViewModel @Inject constructor(
    private val departmentRepository: DepartmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDepartmentUiState())
    val uiState: StateFlow<AddDepartmentUiState> = _uiState.asStateFlow()

    fun addDepartment(name: String, description: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Department name is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            departmentRepository.addDepartment(
                departmentName = name,
                description = description.takeIf { it.isNotBlank() }
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, error = err.message) }
            }
        }
    }

    fun resetState() {
        _uiState.update { AddDepartmentUiState() }
    }
}
