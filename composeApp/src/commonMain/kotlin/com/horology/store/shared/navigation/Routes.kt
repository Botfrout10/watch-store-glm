package com.horology.store.shared.navigation

import kotlinx.serialization.Serializable

/** Type-safe routes (serialization plugin). Tabs live inside the shell; detail is full-bleed. */
@Serializable
data object Home

@Serializable
data object Catalog

@Serializable
data object Favorites

@Serializable
data object Cart

@Serializable
data class ProductDetail(val productId: String)
