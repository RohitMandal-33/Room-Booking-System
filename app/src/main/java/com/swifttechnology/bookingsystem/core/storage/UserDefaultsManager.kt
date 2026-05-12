package com.swifttechnology.bookingsystem.core.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.swifttechnology.bookingsystem.core.designsystem.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPrefsStore by preferencesDataStore(name = "user_defaults")


class UserDefaultsManager(private val context: Context) {

    // theme settings
    private val DARK_MODE_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("dark_mode")
    private val THEME_MODE_KEY: Preferences.Key<String> = stringPreferencesKey("theme_mode")

    val isDarkModeEnabled: Flow<Boolean> =
        context.userPrefsStore.data.map { it[DARK_MODE_KEY] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.userPrefsStore.edit { it[DARK_MODE_KEY] = enabled }
    }

    val themeMode: Flow<ThemeMode> =
        context.userPrefsStore.data.map { prefs ->
            val stored = prefs[THEME_MODE_KEY]
            when (stored?.uppercase()) {
                "LIGHT" -> ThemeMode.LIGHT
                "DARK" -> ThemeMode.DARK
                "SYSTEM" -> ThemeMode.SYSTEM
                null -> {
                    // Backward compatible default:
                    // if old boolean was enabled, treat it as DARK; otherwise SYSTEM.
                    if (prefs[DARK_MODE_KEY] == true) ThemeMode.DARK else ThemeMode.SYSTEM
                }
                else -> ThemeMode.SYSTEM
            }
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.userPrefsStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.name
            // Keep legacy boolean in sync for any old readers.
            when (mode) {
                ThemeMode.DARK -> prefs[DARK_MODE_KEY] = true
                ThemeMode.LIGHT -> prefs[DARK_MODE_KEY] = false
                ThemeMode.SYSTEM -> {
                    // don't force a value; leave legacy key as is
                }
            }
        }
    }

    // locale / language
    private val LOCALE_KEY: Preferences.Key<String> = stringPreferencesKey("locale")

    val locale: Flow<String> =
        context.userPrefsStore.data.map { it[LOCALE_KEY] ?: "en" }

    suspend fun setLocale(locale: String) {
        context.userPrefsStore.edit { it[LOCALE_KEY] = locale }
    }

    // onboarding status
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

    // login persistence (remember me)
    private val REMEMBER_ME_EMAIL_KEY = stringPreferencesKey("remember_me_email")
    private val REMEMBER_ME_ENABLED_KEY = booleanPreferencesKey("remember_me_enabled")

    val rememberMeEmail: Flow<String?> =
        context.userPrefsStore.data.map { it[REMEMBER_ME_EMAIL_KEY] }

    val isRememberMeEnabled: Flow<Boolean> =
        context.userPrefsStore.data.map { it[REMEMBER_ME_ENABLED_KEY] ?: false }

    suspend fun saveRememberMe(email: String, enabled: Boolean) {
        context.userPrefsStore.edit { prefs ->
            prefs[REMEMBER_ME_ENABLED_KEY] = enabled
            if (enabled) {
                prefs[REMEMBER_ME_EMAIL_KEY] = email
            } else {
                prefs.remove(REMEMBER_ME_EMAIL_KEY)
            }
        }
    }

    suspend fun clearRememberMe() {
        context.userPrefsStore.edit { prefs ->
            prefs.remove(REMEMBER_ME_EMAIL_KEY)
            prefs[REMEMBER_ME_ENABLED_KEY] = false
        }
    }
}
