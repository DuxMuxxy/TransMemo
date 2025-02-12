package com.chrysalide.transmemo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.chrysalide.transmemo.database.entity.ProductDBEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Transaction
    @Query("SELECT * FROM products")
    fun observeAll(): Flow<List<ProductDBEntity>>

    @Query("SELECT * FROM products WHERE inUse = 1")
    suspend fun getInUseProducts(): List<ProductDBEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getBy(id: Int): ProductDBEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductDBEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductDBEntity): Long

    @Delete
    suspend fun delete(product: ProductDBEntity)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Update
    suspend fun update(productDBEntity: ProductDBEntity)

    @Query("DELETE FROM sqlite_sequence WHERE name = 'products'")
    suspend fun deletePrimaryKeyIndex()
}
