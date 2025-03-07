package com.chrysalide.transmemo.presentation.products.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.design.DatePickerDialog
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TimePickerDialog
import com.chrysalide.transmemo.presentation.extension.dayTimeOfIntake
import com.chrysalide.transmemo.presentation.extension.doneDate
import com.chrysalide.transmemo.presentation.extension.getAllMoleculeNames
import com.chrysalide.transmemo.presentation.extension.getAllUnitNames
import com.chrysalide.transmemo.presentation.extension.isValidDecimalValue
import com.chrysalide.transmemo.presentation.extension.isValidIntegerValue
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.extension.productName
import com.chrysalide.transmemo.presentation.extension.stripTrailingZeros
import com.chrysalide.transmemo.presentation.extension.unitName
import com.chrysalide.transmemo.presentation.products.DropDownValueRow
import com.chrysalide.transmemo.presentation.products.SwitchRow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductModal(
    onDismiss: () -> Unit,
    onAddedProduct: () -> Unit,
    viewModel: AddProductViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = Modifier.statusBarsPadding(),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = uiState is AddProductUiState.ProductDetails)
    ) {
        when (val state = uiState) {
            is AddProductUiState.ProductDetails -> {
                AddProductView(
                    product = state.product,
                    dismiss = onDismiss,
                    addProduct = viewModel::addProduct,
                    sheetState = sheetState
                )
            }

            is AddProductUiState.SaveIntake -> {
                DoIntakeView(
                    intake = state.intake,
                    dismiss = onDismiss,
                    backToProductDetails = viewModel::backToProductDetails,
                    confirmIntake = {
                        viewModel.saveIntake(it)
                        onAddedProduct()
                    },
                    sheetState = sheetState
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun AddProductView(
    product: Product,
    dismiss: () -> Unit,
    addProduct: (Product) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var isExtended by remember { mutableStateOf(false) }
    var editingProduct by remember(product) { mutableStateOf(product) }
    var intakePeriodicityValue by remember { mutableStateOf(editingProduct.intakeInterval.toString()) }
    var dosingPerIntakeValue by remember { mutableStateOf(editingProduct.dosePerIntake.stripTrailingZeros()) }
    var capacityValue by remember { mutableStateOf(editingProduct.capacity.stripTrailingZeros()) }
    var expirationDaysValue by remember { mutableStateOf(editingProduct.expirationDays.toString()) }
    var alertDelayValue by remember { mutableStateOf(editingProduct.alertDelay.toString()) }
    val isIntakePeriodicityValid = intakePeriodicityValue.isValidIntegerValue()
    val isDosingPerIntakeValid = dosingPerIntakeValue.isValidDecimalValue()
    val isCapacityValid = capacityValue.isValidDecimalValue()
    val isExpirationDaysValid = expirationDaysValue.isValidIntegerValue()
    val isAlertDelayValid = alertDelayValue.isValidIntegerValue()
    val daysSuffix = stringResource(string.global_days)

    var showTimeOfIntakePicker by remember { mutableStateOf(false) }
    if (showTimeOfIntakePicker) {
        TimePickerDialog(
            initialTime = editingProduct.timeOfIntake,
            onDismiss = { showTimeOfIntakePicker = false },
            onConfirm = {
                editingProduct = editingProduct.copy(timeOfIntake = it)
                showTimeOfIntakePicker = false
            }
        )
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        ValueTextField(
            title = stringResource(string.feature_product_name),
            value = editingProduct.name,
            onValueChange = { editingProduct = editingProduct.copy(name = it) },
            focusManager = focusManager
        )
        Spacer(modifier = Modifier.height(24.dp))
        val isMoleculeDropDownExtended = remember { mutableStateOf(false) }
        DropDownValueRow(
            title = stringResource(string.feature_product_molecule),
            value = editingProduct.moleculeName(),
            isDropDownExtended = isMoleculeDropDownExtended
        ) {
            getAllMoleculeNames().forEach { (molecule, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        editingProduct = editingProduct.copy(molecule = molecule)
                        isMoleculeDropDownExtended.value = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        SwitchRow(
            text = stringResource(string.feature_products_beeing_used),
            checked = editingProduct.inUse,
            onCheckedChange = { editingProduct = editingProduct.copy(inUse = it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SwitchRow(
            text = stringResource(string.feature_products_handle_side),
            checked = editingProduct.handleSide,
            onCheckedChange = { editingProduct = editingProduct.copy(handleSide = it) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        ValueTextField(
            title = stringResource(string.feature_products_intake_periodicity),
            value = intakePeriodicityValue,
            onValueChange = {
                intakePeriodicityValue = it
                if (it.isValidIntegerValue()) {
                    editingProduct = editingProduct.copy(intakeInterval = it.toInt())
                }
            },
            unit = daysSuffix,
            isError = !isIntakePeriodicityValid,
            imeAction = if (isExtended) ImeAction.Next else ImeAction.Done,
            focusManager = focusManager
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(string.feature_products_intake_at_day_time))
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = { showTimeOfIntakePicker = true }) {
                Text(editingProduct.dayTimeOfIntake())
            }
        }

        AnimatedVisibility(isExtended) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                val isUnitDropDownExtended = remember { mutableStateOf(false) }
                DropDownValueRow(
                    title = stringResource(string.feature_product_unit),
                    value = editingProduct.unitName(),
                    isDropDownExtended = isUnitDropDownExtended
                ) {
                    getAllUnitNames().forEach { (unit, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                editingProduct = editingProduct.copy(unit = unit)
                                isUnitDropDownExtended.value = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                ValueTextField(
                    title = stringResource(string.feature_products_dosing_per_intake),
                    value = dosingPerIntakeValue,
                    unit = editingProduct.unitName(),
                    onValueChange = {
                        dosingPerIntakeValue = it
                        if (it.isValidDecimalValue()) {
                            editingProduct = editingProduct.copy(dosePerIntake = it.toFloat())
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
                    unit = editingProduct.unitName(),
                    onValueChange = {
                        capacityValue = it
                        if (it.isValidDecimalValue()) {
                            editingProduct = editingProduct.copy(capacity = it.toFloat())
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
                            editingProduct = editingProduct.copy(expirationDays = it.toInt())
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
                            editingProduct = editingProduct.copy(alertDelay = it.toInt())
                        }
                    },
                    focusManager = focusManager,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    isError = !isAlertDelayValid
                )

                Spacer(modifier = Modifier.height(16.dp))
                SwitchRow(
                    text = stringResource(string.feature_products_notifications),
                    checked = editingProduct.notifications > 0,
                    onCheckedChange = { editingProduct = editingProduct.copy(notifications = if (it) 1 else 0) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExtended = !isExtended }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!isExtended) {
                    Text(stringResource(string.global_more_details), style = MaterialTheme.typography.labelMedium)
                }
                val icon = if (isExtended) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown
                Icon(icon, contentDescription = null)
            }
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { dismiss() }
                }
            ) {
                Text(text = stringResource(string.global_cancel))
            }
            Spacer(Modifier.width(24.dp))
            Button(
                onClick = { addProduct(editingProduct) },
                enabled = isIntakePeriodicityValid &&
                    isDosingPerIntakeValid &&
                    isCapacityValid &&
                    isExpirationDaysValid &&
                    isAlertDelayValid
            ) { Text(text = stringResource(string.feature_products_add_dialog_add_button)) }
        }
    }
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

@ExperimentalMaterial3Api
@Composable
private fun DoIntakeView(
    intake: Intake,
    confirmIntake: (Intake) -> Unit,
    dismiss: () -> Unit,
    backToProductDetails: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val coroutineScope = rememberCoroutineScope()
    var editableIntake by remember(intake) { mutableStateOf(intake) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var editedDoseValue by remember(intake) { mutableStateOf(editableIntake.realDose.stripTrailingZeros()) }
    val isDoseValid = editedDoseValue.isValidDecimalValue()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Text(intake.productName(), style = MaterialTheme.typography.headlineLarge)
        Text(intake.moleculeName(), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text(stringResource(string.feature_add_product_save_intake_message))
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(editableIntake.realDate) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showDatePickerDialog = true
                        }
                    }
                },
            label = { Text(stringResource(string.intake_planned_date)) },
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Select date")
            },
            value = editableIntake.doneDate(),
            onValueChange = {},
            readOnly = true
        )

        if (editableIntake.plannedDose > 0) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(string.intake_planned_dose)) },
                suffix = { Text(intake.unitName()) },
                value = editedDoseValue,
                onValueChange = { editedDoseValue = it },
                isError = !isDoseValid,
                supportingText = if (!isDoseValid) {
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
                trailingIcon = if (!isDoseValid) {
                    { Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
        }

        if (editableIntake.realSide != IntakeSide.UNDEFINED) {
            Spacer(Modifier.height(32.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    onClick = { editableIntake = editableIntake.copy(realSide = IntakeSide.LEFT) },
                    selected = editableIntake.realSide == IntakeSide.LEFT,
                    label = {
                        Text(stringResource(string.intake_side_left), modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                )
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    onClick = { editableIntake = editableIntake.copy(realSide = IntakeSide.RIGHT) },
                    selected = editableIntake.realSide == IntakeSide.RIGHT,
                    label = {
                        Text(stringResource(string.intake_side_right), modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                )
            }
        }

        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = backToProductDetails) { Text(stringResource(string.global_return)) }
            Spacer(Modifier.width(24.dp))
            Button(
                onClick = {
                    editableIntake = editableIntake.copy(
                        plannedDose = editedDoseValue.toFloat(),
                        realDose = editedDoseValue.toFloat()
                    )
                    confirmIntake(editableIntake)
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { dismiss() }
                },
                enabled = isDoseValid
            ) { Text(stringResource(string.global_confirm)) }
        }
    }

    if (showDatePickerDialog) {
        DatePickerDialog(
            initialSelectedDate = editableIntake.realDate,
            onDateSelected = {
                editableIntake = editableIntake.copy(
                    plannedDate = it,
                    realDate = it
                )
            },
            onDismiss = { showDatePickerDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun AddProductDialogPreview() {
    AddProductView(
        product = Product.default(),
        dismiss = {},
        addProduct = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun DoIntakeViewPreview() {
    DoIntakeView(
        intake = Intake(
            product = Product.default().copy(
                name = "Testosterone"
            ),
            realDose = 10f,
            plannedDose = 10f,
            realDate = getCurrentLocalDate(),
            plannedDate = getCurrentLocalDate(),
            realSide = IntakeSide.LEFT,
            plannedSide = IntakeSide.LEFT
        ),
        dismiss = {},
        confirmIntake = {},
        backToProductDetails = {}
    )
}
