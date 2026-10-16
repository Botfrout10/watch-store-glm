package com.horology.store.shared.data

import com.horology.store.shared.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class CatalogDto(
    val version: Int,
    val generatedAt: String,
    val products: List<WatchProduct>,
)

/** Bundled catalog reader — offline, deterministic, cached after first load. */
class CatalogRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()

    private var cache: List<WatchProduct>? = null

    suspend fun products(): List<WatchProduct> {
        cache?.let { return it }
        return mutex.withLock {
            cache ?: load().also { cache = it }
        }
    }

    suspend fun byId(id: String): WatchProduct? = products().firstOrNull { it.id == id }

    suspend fun featured(): List<WatchProduct> =
        products().filter { it.id in FeaturedProducts }

    suspend fun byCategory(category: String): List<WatchProduct> =
        if (category == Categories.All) products()
        else products().filter { it.category == category }

    suspend fun byHouse(house: String): List<WatchProduct> =
        products().filter { it.house == house }

    suspend fun search(query: String): List<WatchProduct> {
        val q = query.trim()
        if (q.isEmpty()) return products()
        return products().filter { p ->
            listOf(p.name, p.house, p.reference, p.collection, p.category)
                .any { it.contains(q, ignoreCase = true) }
        }
    }

    private suspend fun load(): List<WatchProduct> = withContext(Dispatchers.Default) {
        val bytes = Res.readBytes("files/catalog.json")
        json.decodeFromString<CatalogDto>(bytes.decodeToString()).products
    }
}
