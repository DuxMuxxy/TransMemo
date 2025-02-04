package com.chrysalide.transmemo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrysalide.transmemo.database.entity.IntakeDBEntity

@Dao
interface IntakeDao {
    @Query("SELECT * FROM intakes")
    suspend fun getAll(): List<IntakeDBEntity>

    @Query("SELECT * FROM intakes WHERE id = :id")
    suspend fun getById(id: Int): IntakeDBEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<IntakeDBEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: IntakeDBEntity)

    @Delete
    suspend fun delete(product: IntakeDBEntity)

    @Query("DELETE FROM intakes")
    suspend fun deleteAll()
}
