package com.horology.store.shared.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import kotlin.concurrent.Volatile

/** Context bridge — `androidApp.MainActivity` calls init before any composition. */
object AndroidShared {
    @Volatile
    private var contextRef: Context? = null
    val appContext: Context
        get() = requireNotNull(contextRef) { "AndroidShared.init(context) was not called" }

    fun init(context: Context) {
        contextRef = context.applicationContext
    }
}

actual fun createHorologyDatabase(): HorologyDatabase =
    Room.databaseBuilder<HorologyDatabase>(AndroidShared.appContext, "horology.db")
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

actual fun createPreferencesDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = {
            (AndroidShared.appContext.filesDir.resolve("horology.preferences_pb").path).toPath()
        },
    )

actual fun nowMs(): Long = System.currentTimeMillis()
