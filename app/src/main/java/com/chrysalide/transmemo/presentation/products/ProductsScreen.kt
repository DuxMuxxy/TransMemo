package com.chrysalide.transmemo.presentation.products

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.core.model.entities.ProductEntity
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.extension.alertDelay
import com.chrysalide.transmemo.presentation.extension.containerCapacity
import com.chrysalide.transmemo.presentation.extension.dosePerIntake
import com.chrysalide.transmemo.presentation.extension.expirationDate
import com.chrysalide.transmemo.presentation.extension.intakeInterval
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.extension.unitName
import com.chrysalide.transmemo.presentation.products.ProductsUiState.Loading
import com.chrysalide.transmemo.presentation.products.ProductsUiState.Products
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = koinViewModel(),
) {
    val productsUiState by viewModel.productsUiState.collectAsStateWithLifecycle()

    ProductsView(
        productsUiState,
        saveProduct = viewModel::saveProduct,
    )
}

@Composable
private fun ProductsView(
    productsUiState: ProductsUiState,
    saveProduct: (ProductEntity) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (productsUiState) {
            is Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator()
                }
            }

            is Products -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                ) {
                    itemsIndexed(items = productsUiState.products) { index, product ->
                        ProductCard(product, saveProduct)
                        if (index < productsUiState.products.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductEntity,
    saveProduct: (ProductEntity) -> Unit
) {
    var isExtented by remember { mutableStateOf(true) }
    var isEditing by remember { mutableStateOf(false) }
    var editableProduct by remember(isEditing) { mutableStateOf(product) }
    var editedIntakePeriodicityValue by remember(isEditing) { mutableStateOf(product.intakeInterval.toString()) }
    var editedDosingPerIntakeValue by remember(isEditing) { mutableStateOf(product.dosePerIntake.toString()) }
    var editedCapacityValue by remember(isEditing) { mutableStateOf(product.capacity.toString()) }
    var editedExpirationDaysValue by remember(isEditing) { mutableStateOf(product.expirationDays.toString()) }
    var editedAlertDelayValue by remember(isEditing) { mutableStateOf(product.alertDelay.toString()) }
    val isIntakePeriodicityValid = editedIntakePeriodicityValue.isValidIntegerValue()
    val isDosingPerIntakeValid = editedDosingPerIntakeValue.isValidDecimalValue()
    val isCapacityValid = editedCapacityValue.isValidDecimalValue()
    val isExpirationDaysValid = editedExpirationDaysValue.isValidIntegerValue()
    val isAlertDelayValid = editedAlertDelayValue.isValidIntegerValue()

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { isExtented = !isExtented },
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row {
                Column {
                    Text(product.name, style = MaterialTheme.typography.headlineMedium)
                    Text(product.moleculeName(), style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.weight(1f).widthIn(min = 16.dp))
                AnimatedVisibility(!isEditing) {
                    val editButtonContentDescription = stringResource(string.feature_products_edit_button_content_description)
                    IconButton(onClick = { isEditing = true },) {
                        Icon(Icons.Rounded.Edit, contentDescription = editButtonContentDescription)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val focusManager = LocalFocusManager.current
            val unitName = product.unitName()
            val daysSuffix = stringResource(string.global_days)

            SwitchRow(
                text = stringResource(string.feature_products_beeing_used),
                checked = editableProduct.inUse,
                enabled = isEditing,
                onCheckedChange = { editableProduct = editableProduct.copy(inUse = it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            SwitchRow(
                text = stringResource(string.feature_products_handle_side),
                checked = editableProduct.handleSide,
                enabled = isEditing,
                onCheckedChange = { editableProduct = editableProduct.copy(handleSide = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))


            AnimatedContent(isEditing) { isBeingEdited ->
                Column {
                    TextValueRow(
                        title = stringResource(string.feature_products_intake_periodicity),
                        valueText = editableProduct.intakeInterval(),
                        value = editedIntakePeriodicityValue,
                        unit = daysSuffix,
                        isEditing = isBeingEdited,
                        onValueChange = { editedIntakePeriodicityValue = it },
                        focusManager = focusManager,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                        isError = !isIntakePeriodicityValid
                    )

                    AnimatedVisibility(isExtented) {
                        Column {
                            Spacer(modifier = Modifier.height(24.dp))

                            TextValueRow(
                                title = stringResource(string.feature_products_dosing_per_intake),
                                valueText = editableProduct.dosePerIntake(),
                                value = editedDosingPerIntakeValue,
                                unit = unitName,
                                isEditing = isBeingEdited,
                                onValueChange = { editedDosingPerIntakeValue = it },
                                focusManager = focusManager,
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next,
                                isError = !isDosingPerIntakeValid
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            TextValueRow(
                                title = stringResource(string.feature_products_capacity),
                                valueText = editableProduct.containerCapacity(),
                                value = editedCapacityValue,
                                unit = unitName,
                                isEditing = isBeingEdited,
                                onValueChange = { editedCapacityValue = it },
                                focusManager = focusManager,
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next,
                                isError = !isCapacityValid
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            TextValueRow(
                                title = stringResource(string.feature_products_expiration_after_opening),
                                valueText = editableProduct.expirationDate(),
                                value = editedExpirationDaysValue,
                                unit = daysSuffix,
                                isEditing = isBeingEdited,
                                onValueChange = { editedExpirationDaysValue = it },
                                focusManager = focusManager,
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                                isError = !isExpirationDaysValid
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            TextValueRow(
                                title = stringResource(string.feature_products_alert_delay),
                                valueText = editableProduct.alertDelay(),
                                value = editedAlertDelayValue,
                                unit = daysSuffix,
                                isEditing = isBeingEdited,
                                onValueChange = { editedAlertDelayValue = it },
                                focusManager = focusManager,
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                                isError = !isAlertDelayValid
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val icon = if (isExtented) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown
                        Icon(icon, contentDescription = null)
                    }

                    AnimatedVisibility(isEditing) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                onClick = {
                                    isEditing = false
                                    editableProduct = product
                                },
                            ) { Text(stringResource(string.global_cancel)) }
                            TextButton(
                                enabled = isIntakePeriodicityValid &&
                                    isDosingPerIntakeValid &&
                                    isCapacityValid &&
                                    isExpirationDaysValid &&
                                    isAlertDelayValid,
                                onClick = {
                                    editableProduct = editableProduct.copy(
                                        intakeInterval = editedIntakePeriodicityValue.toInt(),
                                        dosePerIntake = editedDosingPerIntakeValue.toFloat(),
                                        capacity = editedCapacityValue.toFloat(),
                                        expirationDays = editedExpirationDaysValue.toInt(),
                                        alertDelay = editedAlertDelayValue.toInt()
                                    )
                                    saveProduct(editableProduct)
                                    isEditing = false
                                },
                            ) { Text(stringResource(string.global_save)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(
    text: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun TextValueRow(
    title: String,
    valueText: String,
    value: String,
    unit: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    focusManager: FocusManager,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    isError: Boolean
) {
    if (isEditing) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { onValueChange(it) },
            label = { Text(title) },
            suffix = { Text(unit) },
            singleLine = true,
            isError = isError,
            supportingText = if (isError) {
                {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Bad value format",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                null
            },
            trailingIcon = if (isError) {
                { Icon(Icons.Filled.Warning, "error", tint = MaterialTheme.colorScheme.error) }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title)
            HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 24.dp))
            Text(valueText)
        }
    }
}

private fun String.isValidIntegerValue() = isNotBlank() && isDigitsOnly()

private fun String.isValidDecimalValue() = isNotBlank() && matches("^[0-9]+(\\.[0-9]+)?$".toRegex())

@ThemePreviews
@Composable
private fun ProductsScreenLoadingPreviews() {
    TransMemoTheme {
        ProductsView(Loading, {})
    }
}

@ThemePreviews
@Composable
private fun ProductsScreenListPreviews() {
    TransMemoTheme {
        ProductsView(
            Products(
                products = listOf(
                    ProductEntity(
                        name = "Testosterone",
                        molecule = 9,
                        unit = 4,
                        dosePerIntake = 1f,
                        capacity = 2f,
                        expirationDays = 365,
                        intakeInterval = 21,
                        alertDelay = 1,
                        handleSide = true,
                        inUse = true,
                        notifications = 15,
                    ),
                ),
            ),
            saveProduct = {},
        )
    }
}
