package com.chrysalide.transmemo.domain.model

import kotlinx.datetime.LocalDate

data class IntakeToIgnore(
    val date: LocalDate,
    val product: Product
)
