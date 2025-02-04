package com.chrysalide.transmemo.presentation.products

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.extension.alertDelay
import com.chrysalide.transmemo.presentation.extension.containerCapacity
import com.chrysalide.transmemo.presentation.extension.dosePerIntake
import com.chrysalide.transmemo.presentation.extension.expirationDate
import com.chrysalide.transmemo.presentation.extension.intakeInterval
import com.chrysalide.transmemo.presentation.extension.isValidDecimalValue
import com.chrysalide.transmemo.presentation.extension.isValidIntegerValue
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.extension.unitName
import com.chrysalide.transmemo.presentation.products.ProductsUiState.Loading
import com.chrysalide.transmemo.presentation.products.ProductsUiState.Products
import com.chrysalide.transmemo.presentation.products.add.AddProductDialog
import com.chrysalide.transmemo.presentation.products.delete.AskDeleteProductDialog
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import dev.sergiobelda.compose.vectorize.images.Images
import dev.sergiobelda.compose.vectorize.images.icons.rounded.Pill
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = koinViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val productsUiState by viewModel.productsUiState.collectAsStateWithLifecycle()
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddSuccessSnackbar by remember { mutableStateOf(false) }
    var showDeleteProductDialog by remember { mutableStateOf(false) }
    var showDeleteSuccessSnackbar by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onAddedProduct = { showAddSuccessSnackbar = true }
        )
    }

    if (showDeleteProductDialog) {
        AskDeleteProductDialog(
            onDismiss = {
                productToDelete = null
                showDeleteProductDialog = false
            },
            onConfirm = {
                productToDelete?.let { viewModel.deleteProduct(it) }
                showDeleteProductDialog = false
                showDeleteSuccessSnackbar = true
            }
        )
    }

    val addProductSuccessText = stringResource(string.feature_products_add_success)
    val deleteProductSuccessText = stringResource(string.feature_products_delete_success)
    LaunchedEffect(showAddSuccessSnackbar, showDeleteSuccessSnackbar) {
        when {
            showAddSuccessSnackbar -> {
                onShowSnackbar(addProductSuccessText, null)
                showAddSuccessSnackbar = false
            }
            showDeleteSuccessSnackbar -> {
                onShowSnackbar(deleteProductSuccessText, null)
                showDeleteSuccessSnackbar = false
            }
        }
    }

    ProductsView(
        productsUiState,
        saveProduct = viewModel::saveProduct,
        addProduct = { showAddProductDialog = true },
        deleteProduct = {
            productToDelete = it
            showDeleteProductDialog = true
        }
    )
}

@Composable
private fun ProductsView(
    productsUiState: ProductsUiState,
    saveProduct: (Product) -> Unit,
    addProduct: () -> Unit,
    deleteProduct: (Product) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            // Replace by an illustration ?
                            Icon(
                                Images.Icons.Rounded.Pill,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    itemsIndexed(items = productsUiState.products) { index, product ->
                        ProductCard(product, saveProduct, deleteProduct)
                        if (index < productsUiState.products.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                FloatingActionButton(
                    onClick = addProduct,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = stringResource(string.feature_products_add_button_content_description))
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    saveProduct: (Product) -> Unit,
    deleteProduct: (Product) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isExtented by remember { mutableStateOf(false) }
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
            AnimatedContent(isEditing) { isBeingEdited ->
                if (isBeingEdited) {
                    Column {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = editableProduct.name,
                            onValueChange = { editableProduct = editableProduct.copy(name = it) },
                            label = { Text(stringResource(string.feature_product_name)) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next,),
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
                                    .padding(horizontal = 24.dp)
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
                                    text = editableProduct.moleculeName(),
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = isMoleculeDropDownExtended,
                                onDismissRequest = { isMoleculeDropDownExtended = false }
                            ) {
                                stringArrayResource(R.array.molecules).forEachIndexed { index, moleculeName ->
                                    DropdownMenuItem(
                                        text = { Text(moleculeName) },
                                        onClick = {
                                            editableProduct = editableProduct.copy(molecule = index)
                                            isMoleculeDropDownExtended = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var isUnitDropDownExtended by remember { mutableStateOf(false) }
                            Text(stringResource(string.feature_product_unit))
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
                                    ).clickable { isUnitDropDownExtended = !isUnitDropDownExtended }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = editableProduct.unitName(),
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = isUnitDropDownExtended,
                                onDismissRequest = { isUnitDropDownExtended = false }
                            ) {
                                stringArrayResource(R.array.units).forEachIndexed { index, unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            editableProduct = editableProduct.copy(unit = index)
                                            isUnitDropDownExtended = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, style = MaterialTheme.typography.headlineMedium)
                            Text(product.moleculeName(), style = MaterialTheme.typography.titleMedium)
                        }

                        val deleteButtonContentDescription = stringResource(string.feature_products_delete_button_content_description)
                        IconButton(onClick = { deleteProduct(product) }, modifier = Modifier.padding(start = 8.dp)) {
                            Icon(Icons.Rounded.Delete, contentDescription = deleteButtonContentDescription)
                        }
                        val editButtonContentDescription = stringResource(string.feature_products_edit_button_content_description)
                        IconButton(onClick = { isEditing = true },) {
                            Icon(Icons.Rounded.Edit, contentDescription = editButtonContentDescription)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
fun SwitchRow(
    text: String,
    checked: Boolean,
    enabled: Boolean = true,
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
        OutlinedTextField(
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
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title)
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            )
            Text(valueText)
        }
    }
}

@ThemePreviews
@Composable
private fun ProductsScreenLoadingPreviews() {
    TransMemoTheme {
        ProductsView(Loading, {}, {}, {})
    }
}

@ThemePreviews
@Composable
private fun ProductsScreenListPreviews() {
    TransMemoTheme {
        ProductsView(
            Products(
                products = listOf(
                    Product(
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
            addProduct = {},
            deleteProduct = {}
        )
    }
}
