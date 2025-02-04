package com.chrysalide.transmemo.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.domain.model.Product

@Composable
fun Product.moleculeName() = stringArrayResource(R.array.molecules)[molecule]

@Composable
fun Product.dosePerIntake() = "$dosePerIntake ${unitName()}"

@Composable
fun Product.containerCapacity() = "$capacity ${unitName()}"

@Composable
fun Product.intakeInterval() = pluralStringResource(R.plurals.global_days_text_template, intakeInterval, intakeInterval)

@Composable
fun Product.expirationDate() = pluralStringResource(R.plurals.global_days_text_template, expirationDays, expirationDays)

@Composable
fun Product.alertDelay() = pluralStringResource(R.plurals.global_days_text_template, alertDelay, alertDelay)

@Composable
fun Product.unitName() = stringArrayResource(R.array.units)[unit]
