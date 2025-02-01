package com.chrysalide.transmemo.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "wellness"
)
data class WellnessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Int,
    val criteriaId: Int,
    val value: Float
)
