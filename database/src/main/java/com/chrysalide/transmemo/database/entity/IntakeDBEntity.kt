package com.chrysalide.transmemo.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chrysalide.transmemo.domain.model.MeasureUnit

@Entity(
    tableName = "intakes"
)
data class IntakeDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val unit: MeasureUnit,
    val plannedDose: Float,
    val realDose: Float,
    val plannedDate: Int,
    val realDate: Int,
    val plannedSide: Int,
    val realSide: Int
)
