package com.chrysalide.transmemo.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "containers"
)
data class ContainerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val unit: Int,
    val remainingCapacity: Float,
    val usedCapacity: Float,
    val openDate: Int,
    val expirationDate: Int,
    val state: Int
)
