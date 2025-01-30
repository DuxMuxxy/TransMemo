package com.chrysalide.transmemo.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrysalide.transmemo.core.model.WellnessEntity

@Dao
interface WellnessDao {
    @Query("SELECT * FROM wellness")
    suspend fun getAll(): List<WellnessEntity>

    @Query("SELECT * FROM wellness WHERE id = :id")
    suspend fun getById(id: Long): WellnessEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WellnessEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WellnessEntity>)

    @Delete
    suspend fun delete(entity: WellnessEntity)

    @Query("DELETE FROM wellness")
    suspend fun deleteAll()
}
