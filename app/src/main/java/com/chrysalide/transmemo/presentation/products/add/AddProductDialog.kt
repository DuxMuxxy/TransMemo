package com.chrysalide.transmemo.presentation.products.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.extension.getAllMoleculeNames
import com.chrysalide.transmemo.presentation.extension.getAllUnitNames
import com.chrysalide.transmemo.presentation.extension.isValidDecimalValue
import com.chrysalide.transmemo.presentation.extension.isValidIntegerValue
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.extension.stripTrailingZeros
import com.chrysalide.transmemo.presentation.extension.unitName
import com.chrysalide.transmemo.presentation.products.DropDownValueRow
import com.chrysalide.transmemo.presentation.products.SwitchRow
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddedProduct: () -> Unit,
    viewModel: AddProductViewModel = koinViewModel()
) {
    AddProductDialog(
        onDismiss = onDismiss,
        addProduct = { product ->
            viewModel.addProduct(product)
            onAddedProduct()
        }
    )
}

@Composable
private fun AddProductDialog(
    onDismiss: () -> Unit,
    addProduct: (Product) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isExtented by remember { mutableStateOf(false) }
    var product by remember { mutableStateOf(Product.default()) }
    var intakePeriodicityValue by remember { mutableStateOf(product.intakeInterval.toString()) }
    var dosingPerIntakeValue by remember { mutableStateOf(product.dosePerIntake.stripTrailingZeros()) }
    var capacityValue by remember { mutableStateOf(product.capacity.stripTrailingZeros()) }
    var expirationDaysValue by remember { mutableStateOf(product.expirationDays.toString()) }
    var alertDelayValue by remember { mutableStateOf(product.alertDelay.toString()) }
    val isIntakePeriodicityValid = intakePeriodicityValue.isValidIntegerValue()
    val isDosingPerIntakeValid = dosingPerIntakeValue.isValidDecimalValue()
    val isCapacityValid = capacityValue.isValidDecimalValue()
    val isExpirationDaysValid = expirationDaysValue.isValidIntegerValue()
    val isAlertDelayValid = alertDelayValue.isValidIntegerValue()
    val daysSuffix = stringResource(string.global_days)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(string.feature_products_add_dialog_title)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ValueTextField(
                    title = stringResource(string.feature_product_name),
                    value = product.name,
                    onValueChange = { product = product.copy(name = it) },
                    focusManager = focusManager
                )
                Spacer(modifier = Modifier.height(24.dp))
                var isMoleculeDropDownExtended = remember { mutableStateOf(false) }
                DropDownValueRow(
                    title = stringResource(string.feature_product_molecule),
                    value = product.moleculeName(),
                    isDropDownExtended = isMoleculeDropDownExtended
                ) {
                    getAllMoleculeNames().forEach { (molecule, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                product = product.copy(molecule = molecule)
                                isMoleculeDropDownExtended.value = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                SwitchRow(
                    text = stringResource(string.feature_products_beeing_used),
                    checked = product.inUse,
                    onCheckedChange = { product = product.copy(inUse = it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SwitchRow(
                    text = stringResource(string.feature_products_handle_side),
                    checked = product.handleSide,
                    onCheckedChange = { product = product.copy(handleSide = it) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                ValueTextField(
                    title = stringResource(string.feature_products_intake_periodicity),
                    value = intakePeriodicityValue,
                    onValueChange = {
                        intakePeriodicityValue = it
                        if (it.isValidIntegerValue()) {
                            product = product.copy(intakeInterval = it.toInt())
                        }
                    },
                    unit = daysSuffix,
                    isError = !isIntakePeriodicityValid,
                    imeAction = if (isExtented) ImeAction.Next else ImeAction.Done,
                    focusManager = focusManager
                )

                AnimatedVisibility(isExtented) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        var isUnitDropDownExtended = remember { mutableStateOf(false) }
                        DropDownValueRow(
                            title = stringResource(string.feature_product_unit),
                            value = product.unitName(),
                            isDropDownExtended = isUnitDropDownExtended
                        ) {
                            getAllUnitNames().forEach { (unit, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        product = product.copy(unit = unit)
                                        isUnitDropDownExtended.value = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        ValueTextField(
                            title = stringResource(string.feature_products_dosing_per_intake),
                            value = dosingPerIntakeValue,
                            unit = product.unitName(),
                            onValueChange = {
                                dosingPerIntakeValue = it
                                if (it.isValidDecimalValue()) {
                                    product = product.copy(dosePerIntake = it.toFloat())
                                }
                            },
                            focusManager = focusManager,
                            keyboardType = KeyboardType.Decimal,
                            isError = !isDosingPerIntakeValid
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ValueTextField(
                            title = stringResource(string.feature_products_capacity),
                            value = capacityValue,
                            unit = product.unitName(),
                            onValueChange = {
                                capacityValue = it
                                if (it.isValidDecimalValue()) {
                                    product = product.copy(capacity = it.toFloat())
                                }
                            },
                            focusManager = focusManager,
                            keyboardType = KeyboardType.Decimal,
                            isError = !isCapacityValid
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ValueTextField(
                            title = stringResource(string.feature_products_expiration_after_opening),
                            value = expirationDaysValue,
                            unit = daysSuffix,
                            onValueChange = {
                                expirationDaysValue = it
                                if (it.isValidIntegerValue()) {
                                    product = product.copy(expirationDays = it.toInt())
                                }
                            },
                            focusManager = focusManager,
                            keyboardType = KeyboardType.Number,
                            isError = !isExpirationDaysValid
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ValueTextField(
                            title = stringResource(string.feature_products_alert_delay),
                            value = alertDelayValue,
                            unit = daysSuffix,
                            onValueChange = {
                                alertDelayValue = it
                                if (it.isValidIntegerValue()) {
                                    product = product.copy(alertDelay = it.toInt())
                                }
                            },
                            focusManager = focusManager,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                            isError = !isAlertDelayValid
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { isExtented = !isExtented },
                    horizontalArrangement = Arrangement.Center
                ) {
                    val icon = if (isExtented) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    addProduct(product)
                    onDismiss()
                },
                enabled = isIntakePeriodicityValid &&
                    isDosingPerIntakeValid &&
                    isCapacityValid &&
                    isExpirationDaysValid &&
                    isAlertDelayValid
            ) {
                Text(text = stringResource(string.feature_products_add_dialog_add_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(string.global_cancel))
            }
        },
    )
}

@Composable
private fun ValueTextField(
    title: String,
    unit: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    focusManager: FocusManager
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(title) },
        suffix = if (unit != null) {
            { Text(unit) }
        } else {
            null
        },
        singleLine = true,
        isError = isError,
        supportingText = if (isError) {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(string.global_bad_value_format),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        } else {
            null
        },
        trailingIcon = if (isError) {
            { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
    )
}

@ThemePreviews
@Composable
fun AddProductDialogPreview() {
    AddProductDialog(
        onDismiss = {},
        addProduct = {}
    )
}
