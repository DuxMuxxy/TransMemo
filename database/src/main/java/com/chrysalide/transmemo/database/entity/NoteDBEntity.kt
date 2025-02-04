package com.chrysalide.transmemo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes"
)
data class NoteDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Int,
    val text: String
)
