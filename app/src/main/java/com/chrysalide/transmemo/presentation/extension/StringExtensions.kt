package com.chrysalide.transmemo.presentation.extension

import androidx.core.text.isDigitsOnly
import java.math.RoundingMode

fun String.isValidIntegerValue() = isNotBlank() && isDigitsOnly()

fun String.isValidDecimalValue() = isNotBlank() && matches("^[0-9]+(\\.[0-9]+)?$".toRegex())

fun Float.stripTrailingZeros(): String = toBigDecimal().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
