package com.chrysalide.transmemo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "containers"
)
data class ContainerDBEntity(
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
