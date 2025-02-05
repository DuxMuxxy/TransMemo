package com.chrysalide.transmemo.domain.extension

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.todayIn
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun LocalDate.formatToSystemDate(): String {
    val javaLocalDateTime = this.toJavaLocalDate()
    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
    return javaLocalDateTime.format(formatter)
}

fun getCurrentLocalDate(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
