package com.chrysalide.transmemo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chrysalide.transmemo.domain.model.MeasureUnit
import com.chrysalide.transmemo.domain.model.Molecule
import kotlinx.datetime.LocalTime

@Entity(
    tableName = "products"
)
data class ProductDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val molecule: Molecule,
    val unit: MeasureUnit,
    val dosePerIntake: Float,
    val capacity: Float,
    val expirationDays: Int,
    val intakeInterval: Int,
    val timeOfIntake: LocalTime,
    val alertDelay: Int,
    val handleSide: Boolean,
    val inUse: Boolean,
    val notifications: Int
)
