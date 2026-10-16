package com.horology.store.shared.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Toggles produce snapshot booleans for instant heart-burst feedback. */
class FavoritesRepository(private val db: HorologyDatabase) {

    private val dao get() = db.favoriteDao()

    fun ids(): Flow<Set<String>> =
        dao.observeAll().map { rows -> rows.map { it.productId }.toSet() }

    fun full(): Flow<List<FavoriteEntity>> = dao.observeAll()

    /** True when the reference is NOW favorite. */
    suspend fun toggle(productId: String): Boolean {
        val exists = dao.exists(productId)
        return if (exists) {
            dao.delete(productId)
            false
        } else {
            dao.upsert(FavoriteEntity(productId = productId, addedAtEpochMs = nowMs()))
            true
        }
    }

    suspend fun isFavorite(productId: String): Boolean = dao.exists(productId)
}
