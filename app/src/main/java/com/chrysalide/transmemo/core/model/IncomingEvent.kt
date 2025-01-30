package com.chrysalide.transmemo.core.model

import kotlinx.datetime.LocalDate

data class IncomingEvent(
    val title: String,
    val description: String,
    val date: LocalDate
)
