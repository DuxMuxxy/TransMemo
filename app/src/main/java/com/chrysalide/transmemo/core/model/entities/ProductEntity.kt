package com.chrysalide.transmemo.core.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "products"
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val molecule: Int,
    val unit: Int,
    val takeDose: Float,
    val capacity: Float,
    val dlcDays: Int,
    val interval: Int,
    val alertDelay: Int,
    val side: Int,
    val state: Int,
    val notifications: Int
)
