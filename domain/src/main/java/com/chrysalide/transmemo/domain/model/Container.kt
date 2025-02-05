package com.chrysalide.transmemo.domain.model

import kotlinx.datetime.LocalDate

data class Container(
    val id: Int = 0,
    val product: Product,
    val usedCapacity: Float,
    val openDate: LocalDate,
    val state: Int
)
