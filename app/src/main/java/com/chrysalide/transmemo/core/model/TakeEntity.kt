package com.chrysalide.transmemo.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "takes"
)
data class TakeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val unit: Int,
    val plannedDose: Float,
    val realDose: Float,
    val plannedDate: Int,
    val realDate: Int,
    val plannedSide: Int,
    val realSide: Int
)
