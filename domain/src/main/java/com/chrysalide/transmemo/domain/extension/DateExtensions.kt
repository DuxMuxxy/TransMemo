package com.chrysalide.transmemo.domain.extension

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
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

fun Long.toLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.date
}

fun LocalDate.toUTCTimestamp(): Long {
    val localDateTime = this.atTime(0, 0, 0)
    val instant = localDateTime.toInstant(TimeZone.UTC)
    return instant.toEpochMilliseconds()
}
