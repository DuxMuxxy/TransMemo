package com.chrysalide.transmemo.domain.extension

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val timeZone = TimeZone.currentSystemDefault()

fun LocalDate.formatToSystemDate(): String {
    val javaLocalDateTime = this.toJavaLocalDate()
    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
    return javaLocalDateTime.format(formatter)
}

fun LocalDateTime.formatToSystemDate(): String {
    val javaLocalDateTime = this.toJavaLocalDateTime()
    val formatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())
    return javaLocalDateTime.format(formatter)
}

fun getCurrentLocalDate(): LocalDate = Clock.System.todayIn(timeZone)

fun Long.toLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(timeZone)
    return localDateTime.date
}

fun Long.toLocalDateTime(): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(timeZone)
    return localDateTime
}

fun LocalDate.toEpochMillis(): Long {
    val localDateTime = this.atTime(0, 0, 0)
    val instant = localDateTime.toInstant(timeZone)
    return instant.toEpochMilliseconds()
}

fun LocalDate.toMidDayEpochMillis(): Long {
    val localDateTime = this.atTime(12, 0, 0)
    val instant = localDateTime.toInstant(timeZone)
    return instant.toEpochMilliseconds()
}

fun LocalDateTime.toEpochMillis(): Long {
    val instant = toInstant(timeZone)
    return instant.toEpochMilliseconds()
}

fun LocalTime.toEpochMillis(atDate: LocalDate = getCurrentLocalDate()): Long {
    val instant = atDate(atDate).toInstant(timeZone)
    return instant.toEpochMilliseconds()
}

fun LocalDate.isToday() = this == getCurrentLocalDate()
