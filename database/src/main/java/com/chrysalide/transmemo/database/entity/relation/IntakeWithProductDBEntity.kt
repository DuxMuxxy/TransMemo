package com.chrysalide.transmemo.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.chrysalide.transmemo.database.entity.IntakeDBEntity
import com.chrysalide.transmemo.database.entity.ProductDBEntity

data class IntakeWithProductDBEntity(
    @Embedded val intake: IntakeDBEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductDBEntity
)
