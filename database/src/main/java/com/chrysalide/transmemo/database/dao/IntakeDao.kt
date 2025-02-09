package com.chrysalide.transmemo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.chrysalide.transmemo.database.entity.IntakeDBEntity
import com.chrysalide.transmemo.database.entity.relation.IntakeWithProductDBEntity

@Dao
interface IntakeDao {
    @Transaction
    @Query("SELECT * FROM intakes ORDER BY realDate DESC")
    suspend fun getAll(): List<IntakeWithProductDBEntity>

    @Transaction
    @Query("SELECT * FROM intakes WHERE productId = :productId ORDER BY realDate DESC LIMIT 1")
    suspend fun getLastIntakeForProduct(productId: Int): IntakeWithProductDBEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(intakes: List<IntakeDBEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(intake: IntakeDBEntity)

    @Delete
    suspend fun delete(intake: IntakeDBEntity)

    @Query("DELETE FROM intakes")
    suspend fun deleteAll()
}
