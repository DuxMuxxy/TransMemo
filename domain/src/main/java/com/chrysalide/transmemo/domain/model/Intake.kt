package com.chrysalide.transmemo.domain.model

data class Intake(
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
