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
    val dosePerIntake: Float,
    val capacity: Float,
    val expirationDays: Int,
    val intakeInterval: Int,
    val alertDelay: Int,
    val handleSide: Boolean,
    val inUse: Boolean,
    val notifications: Int
) {
    companion object {
        fun default() = ProductEntity(
            name = "",
            molecule = 0,
            unit = 0,
            dosePerIntake = 0f,
            capacity = 0f,
            expirationDays = 0,
            intakeInterval = 21,
            alertDelay = 3,
            handleSide = false,
            inUse = false,
            notifications = 0
        )
    }
}
