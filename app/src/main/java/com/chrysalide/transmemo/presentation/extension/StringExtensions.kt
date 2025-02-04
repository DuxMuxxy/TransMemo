package com.chrysalide.transmemo.presentation.extension

import androidx.core.text.isDigitsOnly

fun String.isValidIntegerValue() = isNotBlank() && isDigitsOnly()

fun String.isValidDecimalValue() = isNotBlank() && matches("^[0-9]+(\\.[0-9]+)?$".toRegex())

fun Float.stripTrailingZeros() = toBigDecimal().stripTrailingZeros().toPlainString()
