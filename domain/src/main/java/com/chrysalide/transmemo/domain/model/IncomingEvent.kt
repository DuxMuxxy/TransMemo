package com.chrysalide.transmemo.domain.model

data class IncomingEvent(
    val title: String,
    val description: String,
    val date: kotlinx.datetime.LocalDate
)
