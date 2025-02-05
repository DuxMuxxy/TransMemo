package com.chrysalide.transmemo.presentation.extension

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
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
