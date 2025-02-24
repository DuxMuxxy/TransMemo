package com.chrysalide.transmemo.presentation.extension

import androidx.compose.runtime.Composable
import com.chrysalide.transmemo.domain.extension.formatToSystemDate
import com.chrysalide.transmemo.domain.model.Container
import kotlinx.datetime.plus

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

fun Container.expirationDateString() = expirationDate().formatToSystemDate()
