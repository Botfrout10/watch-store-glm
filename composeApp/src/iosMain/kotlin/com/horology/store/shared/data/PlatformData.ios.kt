package com.horology.store.shared.data

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import platform.Foundation.NSDate
import platform.Foundation.NSHomeDirectory
import platform.Foundation.timeIntervalSince1970

actual fun createHorologyDatabase(): HorologyDatabase =
    Room.databaseBuilder<HorologyDatabase>(name = "horology.db")
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()

actual fun createPreferencesDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { "${NSHomeDirectory()}/Documents/horology.preferences_pb".toPath() },
    )

actual fun nowMs(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
