package com.chrysalide.transmemo.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrysalide.transmemo.core.model.entities.TakeEntity

@Dao
interface TakeDao {
    @Query("SELECT * FROM takes")
    suspend fun getAll(): List<TakeEntity>

    @Query("SELECT * FROM takes WHERE id = :id")
    suspend fun getById(id: Int): TakeEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<TakeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: TakeEntity)

    @Delete
    suspend fun delete(product: TakeEntity)

    @Query("DELETE FROM takes")
    suspend fun deleteAll()
}
