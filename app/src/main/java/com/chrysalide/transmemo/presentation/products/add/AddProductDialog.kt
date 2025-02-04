package com.chrysalide.transmemo.presentation.products.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.extension.getAllMoleculeNames
import com.chrysalide.transmemo.presentation.extension.isValidIntegerValue
import com.chrysalide.transmemo.presentation.extension.moleculeName
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
    var product by remember { mutableStateOf(Product.default()) }
    var intakePeriodicityValue by remember { mutableStateOf(product.intakeInterval.toString()) }
    val isIntakePeriodicityValid = intakePeriodicityValue.isValidIntegerValue()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(string.feature_products_add_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = product.name,
                    onValueChange = { product = product.copy(name = it) },
                    label = { Text(stringResource(string.feature_product_name)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var isMoleculeDropDownExtended by remember { mutableStateOf(false) }
                    Text(stringResource(string.feature_product_molecule))
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 16.dp)
                            .padding(horizontal = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ).clickable { isMoleculeDropDownExtended = !isMoleculeDropDownExtended }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = product.moleculeName(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = isMoleculeDropDownExtended,
                        onDismissRequest = { isMoleculeDropDownExtended = false }
                    ) {
                        getAllMoleculeNames().forEach { (molecule, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    product = product.copy(molecule = molecule)
                                    isMoleculeDropDownExtended = false
                                }
                            )
                        }
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
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = intakePeriodicityValue,
                    onValueChange = {
                        intakePeriodicityValue = it
                        if (it.isValidIntegerValue()) {
                            product = product.copy(intakeInterval = intakePeriodicityValue.toInt())
                        }
                    },
                    label = { Text(stringResource(string.feature_products_intake_periodicity)) },
                    suffix = { Text(stringResource(string.global_days)) },
                    singleLine = true,
                    isError = !isIntakePeriodicityValid,
                    supportingText = if (!isIntakePeriodicityValid) {
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
                    trailingIcon = if (!isIntakePeriodicityValid) {
                        { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    } else {
                        null
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    )
                )
            }
        },
        confirmButton = {
            Text(
                text = stringResource(string.feature_products_add_dialog_add_button),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        addProduct(product)
                        onDismiss()
                    }
            )
        },
        dismissButton = {
            Text(
                text = stringResource(string.global_cancel),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable(onClick = onDismiss)
            )
        }
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
