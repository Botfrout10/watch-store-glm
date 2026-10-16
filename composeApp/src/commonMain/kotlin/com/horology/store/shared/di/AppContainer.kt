package com.horology.store.shared.di

import com.horology.store.shared.data.CartRepository
import com.horology.store.shared.data.CatalogRepository
import com.horology.store.shared.data.FavoritesRepository
import com.horology.store.shared.data.HorologyDatabase
import com.horology.store.shared.data.OnboardingPrefs
import com.horology.store.shared.data.createHorologyDatabase
import com.horology.store.shared.data.createPreferencesDataStore

/** Manual DI: lazily built singletons, injected via [LocalAppContainer]. */
class AppContainer {

    private val database: HorologyDatabase by lazy { createHorologyDatabase() }

    val catalog: CatalogRepository by lazy { CatalogRepository() }
    val cart: CartRepository by lazy { CartRepository(database) }
    val favorites: FavoritesRepository by lazy { FavoritesRepository(database) }
    val onboarding: OnboardingPrefs by lazy { OnboardingPrefs(createPreferencesDataStore()) }
}
