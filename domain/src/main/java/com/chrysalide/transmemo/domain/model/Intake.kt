package com.chrysalide.transmemo.domain.model

import kotlinx.datetime.LocalDate

data class Intake(
    val id: Int = 0,
    val product: Product,
    val plannedDose: Float,
    val realDose: Float,
    val plannedDate: LocalDate,
    val realDate: LocalDate,
    val plannedSide: IntakeSide,
    val realSide: IntakeSide,
    val isIgnored: Boolean = false
)
