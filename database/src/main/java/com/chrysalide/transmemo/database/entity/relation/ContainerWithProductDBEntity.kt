package com.chrysalide.transmemo.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.chrysalide.transmemo.database.entity.ContainerDBEntity
import com.chrysalide.transmemo.database.entity.ProductDBEntity

data class ContainerWithProductDBEntity(
    @Embedded val container: ContainerDBEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductDBEntity
)
