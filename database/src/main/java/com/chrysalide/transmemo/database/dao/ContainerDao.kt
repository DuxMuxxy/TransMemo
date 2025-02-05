package com.chrysalide.transmemo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.chrysalide.transmemo.database.entity.ContainerDBEntity
import com.chrysalide.transmemo.database.entity.relation.ContainerWithProductDBEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContainerDao {
    @Transaction
    @Query("SELECT * FROM containers")
    fun getAll(): Flow<List<ContainerWithProductDBEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(container: ContainerDBEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(containers: List<ContainerDBEntity>)

    @Delete
    suspend fun delete(container: ContainerDBEntity)

    @Query("DELETE FROM containers")
    suspend fun deleteAll()
}
