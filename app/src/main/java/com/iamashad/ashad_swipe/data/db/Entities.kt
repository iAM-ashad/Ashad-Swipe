package com.iamashad.ashad_swipe.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.iamashad.ashad_swipe.domain.model.Product
import kotlinx.coroutines.flow.Flow

// Represents a product stored in the local Room database
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val image: String?,
    val price: Double,
    val name: String,
    val type: String,
    val tax: Double,
    val localThumb: String? = null, // Path to locally stored image thumbnail
    val isPending: Boolean = false  // Flag to track unsynced (pending) products
)

// Converts ProductEntity to domain model Product
fun ProductEntity.toDomain() = Product(
    image = image,
    price = price,
    name = name,
    type = type,
    tax = tax,
    localThumb = localThumb,
    isPending = isPending
)

// Represents an upload that hasn't been synced with server yet
@Entity(tableName = "pending_uploads")
data class PendingUploadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val price: String,
    val tax: String,
    val imageUri: String?,              // Uri of the selected image
    val createdAt: Long = System.currentTimeMillis(),
    val localProductId: Long? = null    // Optional link to a ProductEntity
)

// DAO for accessing product records
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun observeAll(): Flow<List<ProductEntity>> // Emits product list reactively

    @Query("DELETE FROM products WHERE isPending = 0")
    suspend fun clearNonPending() // Deletes only synced products

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProductEntity>) // Bulk insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ProductEntity): Long // Insert and return row ID

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM products WHERE isPending = 1")
    suspend fun getAllPending(): List<ProductEntity> // Fetch unsynced items
}

// DAO for managing pending uploads
@Dao
interface PendingUploadDao {
    @Query("SELECT * FROM pending_uploads ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingUploadEntity>

    @Insert
    suspend fun insert(item: PendingUploadEntity)

    @Delete
    suspend fun delete(item: PendingUploadEntity)
}

// Room database holder class
@Database(entities = [ProductEntity::class, PendingUploadEntity::class], version = 4)
abstract class SwipeDb : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun pendingDao(): PendingUploadDao
}
