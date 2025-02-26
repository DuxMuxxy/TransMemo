package com.chrysalide.transmemo.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.extension.formatToSystemDate
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide

fun Intake.productName() = product.name

@Composable
fun Intake.moleculeName() = product.moleculeName()

@Composable
fun Intake.unitName() = product.unitName()

fun Intake.doneDate() = realDate.formatToSystemDate()

fun Intake.plannedDate() = plannedDate.formatToSystemDate()

fun Intake.shouldShowPlannedDate() = realDate != plannedDate

@Composable
fun Intake.doneDose() = "${realDose.stripTrailingZeros()} ${unitName()}"

@Composable
fun Intake.plannedDose() = "${plannedDose.stripTrailingZeros()} ${unitName()}"

fun Intake.shouldShowPlannedDose() = realDose != plannedDose && plannedDose > 0

fun Intake.shouldShowSideInfo() = plannedSide != IntakeSide.UNDEFINED || realSide != IntakeSide.UNDEFINED

fun Intake.shouldShowDoseInfo() = plannedDose > 0 || realDose > 0

@Composable
fun Intake.doneSide() = realSide.text()

@Composable
fun Intake.plannedSide() = plannedSide.text()

fun Intake.shouldShowPlannedSide() = realSide != plannedSide

@Composable
private fun IntakeSide.text() = stringResource(
    when (this) {
        IntakeSide.LEFT -> string.intake_side_left
        IntakeSide.RIGHT -> string.intake_side_right
        IntakeSide.UNDEFINED -> string.intake_side_undefined
    }
)
