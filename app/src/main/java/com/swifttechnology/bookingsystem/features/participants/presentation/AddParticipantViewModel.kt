package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.features.department.domain.model.Department
import com.swifttechnology.bookingsystem.features.department.domain.repository.DepartmentRepository
import com.swifttechnology.bookingsystem.features.user.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class AddParticipantUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val departments: List<Department> = emptyList()
)

@HiltViewModel
class AddParticipantViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val departmentRepository: DepartmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddParticipantUiState())
    val uiState: StateFlow<AddParticipantUiState> = _uiState.asStateFlow()

    init {
        fetchDepartments()
    }

    private fun fetchDepartments() {
        viewModelScope.launch {
            departmentRepository.getDepartments()
                .onSuccess { departments ->
                    _uiState.update { it.copy(departments = departments) }
                }
                .onFailure { error ->
                    // Optionally handle error fetching departments
                }
        }
    }

    private fun normalizeEmail(email: String): String =
        email.trim().lowercase(Locale.ROOT)

    private fun normalizePersonName(name: String): String = name.trim()

    private fun normalizePhone(phone: String): String {
        val t = phone.trim()
        return if (t.equals("N/A", ignoreCase = true)) "" else t
    }

    /**
     * Maps UI labels (Admin, Manager, User) and API strings (ADMIN, MANAGER, STAFF) to roleId.
     * IDs follow the backend contract used by signup (1 = admin, 2 = manager, 3 = staff/user).
     */
    private fun resolveRoleId(roleLabelOrApi: String): Long {
        val lower = roleLabelOrApi.trim().lowercase(Locale.ROOT)
        return when (lower) {
            "admin" -> 1L
            "manager" -> 2L
            "user", "staff" -> 3L
            else -> when (roleLabelOrApi.trim().uppercase(Locale.ROOT)) {
                "ADMIN" -> 1L
                "MANAGER" -> 2L
                "STAFF", "USER" -> 3L
                else -> 3L
            }
        }
    }

    fun createParticipant(
        firstname: String,
        lastname: String,
        email: String,
        position: String,
        phoneNo: String,
        password: String,
        role: String,
        departmentId: Long
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            val roleId = resolveRoleId(role)

            val result = userRepository.createUser(
                firstname = normalizePersonName(firstname),
                lastname = normalizePersonName(lastname),
                email = normalizeEmail(email),
                position = normalizePersonName(position),
                phoneNo = normalizePhone(phoneNo),
                password = password.trim(),
                roleId = roleId,
                departmentId = departmentId
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Failed to add member") }
            }
        }
    }

    fun updateParticipant(
        id: Long,
        firstname: String,
        lastname: String,
        email: String,
        position: String,
        phoneNo: String,
        role: String,
        departmentId: Long
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            val roleId = resolveRoleId(role)

            val result = userRepository.updateUser(
                id = id,
                firstname = normalizePersonName(firstname),
                lastname = normalizePersonName(lastname),
                email = normalizeEmail(email),
                position = normalizePersonName(position),
                phoneNo = normalizePhone(phoneNo),
                roleId = roleId,
                departmentId = departmentId
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Failed to update member") }
            }
        }
    }

    fun resetState() {
        _uiState.update { current ->
            current.copy(isLoading = false, isSuccess = false, error = null)
        }
    }
}
