package com.swifttechnology.bookingsystem.app

import androidx.lifecycle.ViewModel
import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenStorage: TokenStorage
) : ViewModel() {
    val forceLogoutEvent: SharedFlow<Unit> = tokenStorage.forceLogoutEvent
}
