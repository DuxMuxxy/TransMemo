package com.chrysalide.transmemo.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.chrysalide.transmemo.domain.model.IntakeSide
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "intakes",
    foreignKeys = [
        ForeignKey(
            entity = ProductDBEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE // Do we use cascade or something else ?
        )
    ],
    indices = [Index("productId", name = "intakes_productId_index")]
)
data class IntakeDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val plannedDose: Float,
    val realDose: Float,
    val plannedDate: LocalDate,
    val realDate: LocalDate,
    val plannedSide: IntakeSide,
    val realSide: IntakeSide
)
