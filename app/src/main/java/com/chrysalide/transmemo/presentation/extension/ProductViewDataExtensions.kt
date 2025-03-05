package com.chrysalide.transmemo.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.MeasureUnit
import com.chrysalide.transmemo.domain.model.Molecule
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.datetime.atDate
import kotlinx.datetime.format
import kotlinx.datetime.toJavaLocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.text.format

@Composable
fun Product.moleculeName() = molecule.name()

@Composable
private fun Molecule.name() =
    stringResource(
        when (this) {
            Molecule.CHLORMADINONE_ACETATE -> string.molecule_chlormadinone_acetate
            Molecule.CYPROTERONE_ACETATE -> string.molecule_cyproterone_acetate
            Molecule.NOMEGESTROL_ACETATE -> string.molecule_nomegestrol_acetate
            Molecule.ANDROSTANOLONE -> string.molecule_androstanolone
            Molecule.BICALUTAMIDE -> string.molecule_bicalutamide
            Molecule.DUTASTERIDE -> string.molecule_dutasteride
            Molecule.ESTRADIOL -> string.molecule_estradiol
            Molecule.FINASTERIDE -> string.molecule_finasteride
            Molecule.TESTOSTERONE -> string.molecule_testosterone
            Molecule.PROGESTERONE -> string.molecule_progesterone
            Molecule.SPIRONOLACTONE -> string.molecule_spironolactone
            Molecule.TRIPTORELIN -> string.molecule_triptorelin
            Molecule.OTHER -> string.molecule_other
        }
    )

@Composable
fun Product.dosePerIntake() = "${dosePerIntake.stripTrailingZeros()} ${unitName()}"

@Composable
fun Product.containerCapacity() = "${capacity.stripTrailingZeros()} ${unitName()}"

@Composable
fun Product.intakeInterval() = pluralStringResource(R.plurals.global_days_text_template, intakeInterval, intakeInterval)

@Composable
fun Product.expirationDate() = pluralStringResource(R.plurals.global_days_text_template, expirationDays, expirationDays)

@Composable
fun Product.alertDelay() = pluralStringResource(R.plurals.global_days_text_template, alertDelay, alertDelay)

@Composable
fun Product.unitName() = unit.name()

@Composable
private fun MeasureUnit.name() =
    stringResource(
        when (this) {
            MeasureUnit.VIAL -> string.unit_vial
            MeasureUnit.PILL -> string.unit_pill
            MeasureUnit.MILLIGRAM -> string.unit_milligram
            MeasureUnit.MILLILITER -> string.unit_milliliter
            MeasureUnit.OZ -> string.unit_oz
            MeasureUnit.PATCH -> string.unit_patch
            MeasureUnit.PUMP -> string.unit_pump
            MeasureUnit.SACHET -> string.unit_sachet
            MeasureUnit.OTHER -> string.unit_other
        }
    )

@Composable
fun getAllMoleculeNames() = Molecule.entries.map { it to it.name() }

@Composable
fun getAllUnitNames() = MeasureUnit.entries.map { it to it.name() }

fun Product.dayTimeOfIntake(): String {
    val localDateTime = timeOfIntake.atDate(getCurrentLocalDate()).toJavaLocalDateTime()
    val zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(java.util.Locale.getDefault())
    return zonedDateTime.format(formatter)
}
