package com.chrysalide.transmemo.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "containers",
    foreignKeys = [
        ForeignKey(
            entity = ProductDBEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE // Do we use cascade or something else ?
        )
    ],
    indices = [Index("productId", name = "containers_productId_index")]
)
data class ContainerDBEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val usedCapacity: Float,
    val openDate: LocalDate,
    val state: Int
)
