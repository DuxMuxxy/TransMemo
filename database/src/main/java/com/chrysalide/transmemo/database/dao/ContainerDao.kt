package com.chrysalide.transmemo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrysalide.transmemo.database.entity.ContainerDBEntity

@Dao
interface ContainerDao {
    @Query("SELECT * FROM containers")
    suspend fun getAll(): List<ContainerDBEntity>

    @Query("SELECT * FROM containers WHERE id = :id")
    suspend fun getById(id: Int): ContainerDBEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(container: ContainerDBEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(containers: List<ContainerDBEntity>)

    @Delete
    suspend fun delete(container: ContainerDBEntity)

    @Query("DELETE FROM containers")
    suspend fun deleteAll()
}
