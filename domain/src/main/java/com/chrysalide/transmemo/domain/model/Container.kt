package com.chrysalide.transmemo.domain.model

data class Container(
    val id: Int = 0,
    val productId: Int,
    val unit: MeasureUnit,
    val remainingCapacity: Float,
    val usedCapacity: Float,
    val openDate: Int,
    val expirationDate: Int,
    val state: Int
)
