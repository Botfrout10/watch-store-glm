package com.horology.store.shared.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "added_at_epoch_ms") val addedAtEpochMs: Long,
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val productId: String,
    @ColumnInfo(name = "added_at_epoch_ms") val addedAtEpochMs: Long,
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY added_at_epoch_ms DESC")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun find(productId: String): CartItemEntity?

    @Query("SELECT COUNT(*) FROM cart_items")
    fun observeCount(): Flow<Int>

    /** Atomic single-statement add-or-increment (KMP-safe: no withTransaction on native). */
    @Query(
        "INSERT INTO cart_items (productId, quantity, added_at_epoch_ms) " +
            "VALUES (:productId, 1, :addedAt) " +
            "ON CONFLICT(productId) DO UPDATE SET quantity = quantity + 1",
    )
    suspend fun upsertAdd(productId: String, addedAt: Long)

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE productId = :productId AND quantity > 0")
    suspend fun decrementOne(productId: String)

    @Query("DELETE FROM cart_items WHERE quantity <= 0")
    suspend fun prune()

    @Upsert
    suspend fun upsert(entity: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun delete(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY added_at_epoch_ms DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :productId)")
    suspend fun exists(productId: String): Boolean

    @Upsert
    suspend fun upsert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :productId")
    suspend fun delete(productId: String)
}
