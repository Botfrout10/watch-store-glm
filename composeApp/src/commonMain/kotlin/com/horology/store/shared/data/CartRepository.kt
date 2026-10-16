package com.horology.store.shared.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Cart mutations. Single-statement DAO ops keep it atomic without Room's
 * Android-only `withTransaction` (fine for a single-writer storefront).
 */
class CartRepository(private val db: HorologyDatabase) {

    private val dao get() = db.cartDao()

    val items: Flow<List<CartItemEntity>> = dao.observeAll()

    val count: Flow<Int> = dao.observeCount()

    /** Adds one unit (or creates the line). Returns the resulting quantity. */
    suspend fun add(productId: String): Int {
        dao.upsertAdd(productId, nowMs())
        return dao.find(productId)?.quantity ?: 0
    }

    /** Decrements by one; lines reaching zero are pruned. Returns the new quantity. */
    suspend fun decrement(productId: String): Int {
        dao.decrementOne(productId)
        dao.prune()
        return dao.find(productId)?.quantity ?: 0
    }

    suspend fun remove(productId: String) {
        dao.delete(productId)
    }

    suspend fun clear() {
        dao.clear()
    }

    fun quantityFor(productId: String): Flow<Int> =
        items.map { lines -> lines.firstOrNull { it.productId == productId }?.quantity ?: 0 }
}
