package com.chrysalide.transmemo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrysalide.transmemo.database.entity.NoteDBEntity

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    suspend fun getAll(): List<NoteDBEntity>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Int): NoteDBEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<NoteDBEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteDBEntity)

    @Delete
    suspend fun delete(note: NoteDBEntity)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}
