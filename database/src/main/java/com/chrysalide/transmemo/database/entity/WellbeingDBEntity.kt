package com.chrysalide.transmemo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "wellness"
)
data class WellbeingDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Int,
    val criteriaId: Int,
    val value: Float
)
