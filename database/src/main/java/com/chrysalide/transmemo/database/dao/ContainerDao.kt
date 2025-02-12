package com.chrysalide.transmemo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.chrysalide.transmemo.database.entity.ContainerDBEntity
import com.chrysalide.transmemo.database.entity.relation.ContainerWithProductDBEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContainerDao {
    @Transaction
    @Query("SELECT * FROM containers WHERE state == 'OPEN'")
    fun observeAll(): Flow<List<ContainerWithProductDBEntity>>

    @Query("SELECT EXISTS(SELECT * FROM containers WHERE productId = :productId)")
    suspend fun existsForProduct(productId: Int): Boolean

    @Transaction
    @Query("SELECT * FROM containers WHERE productId = :productId AND state == 'OPEN'")
    suspend fun getByProductId(productId: Int): ContainerWithProductDBEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(container: ContainerDBEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(containers: List<ContainerDBEntity>)

    @Update
    suspend fun update(container: ContainerDBEntity)

    @Delete
    suspend fun delete(container: ContainerDBEntity)

    @Query("DELETE FROM containers")
    suspend fun deleteAll()
}
