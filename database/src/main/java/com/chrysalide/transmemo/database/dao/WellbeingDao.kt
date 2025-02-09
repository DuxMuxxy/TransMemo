package com.chrysalide.transmemo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrysalide.transmemo.database.entity.WellbeingDBEntity

@Dao
interface WellbeingDao {
    @Query("SELECT * FROM wellbeing")
    suspend fun getAll(): List<WellbeingDBEntity>

    @Query("SELECT * FROM wellbeing WHERE id = :id")
    suspend fun getById(id: Long): WellbeingDBEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wellbeing: WellbeingDBEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WellbeingDBEntity>)

    @Delete
    suspend fun delete(wellbeing: WellbeingDBEntity)

    @Query("DELETE FROM wellbeing")
    suspend fun deleteAll()
}
