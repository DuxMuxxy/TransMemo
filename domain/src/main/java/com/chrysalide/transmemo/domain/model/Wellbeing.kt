package com.chrysalide.transmemo.domain.model

data class Wellbeing(
    val id: Int = 0,
    val date: Int,
    val criteriaId: Int,
    val value: Float
)
