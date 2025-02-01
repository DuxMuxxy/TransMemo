package com.chrysalide.transmemo.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrysalide.transmemo.core.model.entities.ContainerEntity

@Dao
interface ContainerDao {
    @Query("SELECT * FROM containers")
    suspend fun getAll(): List<ContainerEntity>

    @Query("SELECT * FROM containers WHERE id = :id")
    suspend fun getById(id: Int): ContainerEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(container: ContainerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(containers: List<ContainerEntity>)

    @Delete
    suspend fun delete(container: ContainerEntity)

    @Query("DELETE FROM containers")
    suspend fun deleteAll()
}
