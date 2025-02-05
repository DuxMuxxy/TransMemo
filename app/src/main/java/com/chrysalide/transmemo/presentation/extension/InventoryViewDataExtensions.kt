package com.chrysalide.transmemo.presentation.extension

import androidx.compose.runtime.Composable
import com.chrysalide.transmemo.domain.extension.formatToSystemDate
import com.chrysalide.transmemo.domain.model.Container
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import kotlin.math.roundToInt

fun Container.productName() = product.name

@Composable
fun Container.moleculeName() = product.moleculeName()

@Composable
fun Container.unitName() = product.unitName()

@Composable
fun Container.capacity() = product.containerCapacity()

@Composable
fun Container.usedCapacity() = "${usedCapacity.stripTrailingZeros()} ${unitName()}"

@Composable
fun Container.remainingCapacity() = "${(product.capacity - usedCapacity).stripTrailingZeros()} ${unitName()}"

fun Container.openDate() = openDate.formatToSystemDate()

fun Container.expirationDate() = openDate.plus(DatePeriod(days = product.expirationDays)).formatToSystemDate()

fun Container.emptyDate(): String {
    val remainingCapacity = product.capacity - usedCapacity
    val remainingIntakes = (remainingCapacity / product.dosePerIntake).roundToInt()
    val emptyDate = openDate.plus(DatePeriod(days = remainingIntakes * product.intakeInterval))
    return emptyDate.formatToSystemDate()
}
