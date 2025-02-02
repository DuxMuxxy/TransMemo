package com.chrysalide.transmemo.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.core.model.entities.ProductEntity

@Composable
fun ProductEntity.moleculeName() = stringArrayResource(R.array.molecules)[molecule]

@Composable
fun ProductEntity.dosePerIntake() = "$dosePerIntake ${unitName()}"

@Composable
fun ProductEntity.containerCapacity() = "$capacity ${unitName()}"

@Composable
fun ProductEntity.intakeInterval() = pluralStringResource(R.plurals.global_days_text_template, intakeInterval, intakeInterval)

@Composable
fun ProductEntity.expirationDate() = pluralStringResource(R.plurals.global_days_text_template, expirationDays, expirationDays)

@Composable
fun ProductEntity.alertDelay() = pluralStringResource(R.plurals.global_days_text_template, alertDelay, alertDelay)

@Composable
fun ProductEntity.unitName() = stringArrayResource(R.array.units)[unit]
