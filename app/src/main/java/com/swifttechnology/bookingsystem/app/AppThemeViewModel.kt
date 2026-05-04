package com.swifttechnology.bookingsystem.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swifttechnology.bookingsystem.core.designsystem.ThemeMode
import com.swifttechnology.bookingsystem.core.storage.UserDefaultsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val userDefaultsManager: UserDefaultsManager
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> =
        userDefaultsManager.themeMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeMode.SYSTEM
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { userDefaultsManager.setThemeMode(mode) }
    }
}

