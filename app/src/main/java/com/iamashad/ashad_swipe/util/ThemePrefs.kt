package com.iamashad.ashad_swipe.util

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Name of the DataStore file
private const val DS_NAME = "user_prefs"

// Extension property to access this app's DataStore instance
private val Context.dataStore by preferencesDataStore(name = DS_NAME)

/**
 * Stores and retrieves user theme preferences (e.g., dynamic color toggle).
 *
 * Uses Jetpack DataStore for asynchronous and type-safe persistence.
 */
object ThemePrefs {

    private val KEY_DYNAMIC = booleanPreferencesKey("dynamic_color_enabled")

    /**
     * Continuously emits the current dynamic color setting.
     *
     * Default = false (dynamic color disabled)
     */
    fun dynamicColorFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs: Preferences ->
            prefs[KEY_DYNAMIC] ?: false
        }

    /**
     * Saves the user’s dynamic color preference.
     */
    suspend fun setDynamicColor(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[KEY_DYNAMIC] = enabled }
    }
}
