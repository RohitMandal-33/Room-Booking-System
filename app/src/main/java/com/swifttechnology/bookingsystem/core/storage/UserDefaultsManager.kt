package com.swifttechnology.bookingsystem.core.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPrefsStore by preferencesDataStore(name = "user_defaults")


class UserDefaultsManager(private val context: Context) {

    // ── Theme ──────────────────────────────────────────────────────────────
    private val DARK_MODE_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("dark_mode")

    val isDarkModeEnabled: Flow<Boolean> =
        context.userPrefsStore.data.map { it[DARK_MODE_KEY] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.userPrefsStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    // ── Locale ─────────────────────────────────────────────────────────────
    private val LOCALE_KEY: Preferences.Key<String> = stringPreferencesKey("locale")

    val locale: Flow<String> =
        context.userPrefsStore.data.map { it[LOCALE_KEY] ?: "en" }

    suspend fun setLocale(locale: String) {
        context.userPrefsStore.edit { it[LOCALE_KEY] = locale }
    }

    // ── Onboarding ─────────────────────────────────────────────────────────
    private val ONBOARDING_DONE_KEY: Preferences.Key<Boolean> =
        booleanPreferencesKey("onboarding_done")

    val isOnboardingDone: Flow<Boolean> =
        context.userPrefsStore.data.map { it[ONBOARDING_DONE_KEY] ?: false }

    suspend fun setOnboardingDone(done: Boolean) {
        context.userPrefsStore.edit { it[ONBOARDING_DONE_KEY] = done }
    }

    /** Wipe all user preferences (call on logout). */
    suspend fun clear() {
        context.userPrefsStore.edit { it.clear() }
    }
}
