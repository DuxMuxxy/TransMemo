package com.chrysalide.transmemo.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDate.Formats
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

internal class Converters {
    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value, Formats.ISO)

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.format(Formats.ISO)

    @TypeConverter
    fun toLocalTime(value: String): LocalTime = LocalTime.parse(value, LocalTime.Formats.ISO)

    @TypeConverter
    fun fromLocalTime(date: LocalTime): String = date.format(LocalTime.Formats.ISO)
}
