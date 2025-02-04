package com.chrysalide.transmemo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "products"
)
data class ProductDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val molecule: Int,
    val unit: Int,
    val dosePerIntake: Float,
    val capacity: Float,
    val expirationDays: Int,
    val intakeInterval: Int,
    val alertDelay: Int,
    val handleSide: Boolean,
    val inUse: Boolean,
    val notifications: Int
)
