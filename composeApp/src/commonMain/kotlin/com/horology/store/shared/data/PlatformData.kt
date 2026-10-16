package com.horology.store.shared.data

/**
 * Platform seams. Android gets a Context-backed Room/DataStore; iOS builds its
 * own file paths. Implementation lives in androidMain/iosMain.
 */
expect fun createHorologyDatabase(): HorologyDatabase

expect fun createPreferencesDataStore(): androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>

expect fun nowMs(): Long
