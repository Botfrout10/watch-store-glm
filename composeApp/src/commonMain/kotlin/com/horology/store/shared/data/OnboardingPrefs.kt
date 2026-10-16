package com.horology.store.shared.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

/** Single persisted flag (switch: has the buyer walked the boutique rails yet). */
class OnboardingPrefs(private val dataStore: DataStore<Preferences>) {

    private val completedKey = booleanPreferencesKey("onboarding_completed")

    val completed: Flow<Boolean> =
        dataStore.data
            .catch { throwable ->
                if (throwable is IOException) emit(emptyPreferences())
                else throw throwable
            }
            .map { prefs -> prefs[completedKey] == true }

    suspend fun complete() {
        dataStore.edit { prefs -> prefs[completedKey] = true }
    }

    suspend fun reset() {
        dataStore.edit { prefs -> prefs.remove(completedKey) }
    }
}
