package com.chrysalide.transmemo.presentation.calendar.dointake

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import com.chrysalide.transmemo.domain.extension.toEpochMillis
import com.chrysalide.transmemo.domain.extension.toLocalDate
import com.chrysalide.transmemo.domain.model.DateIntakeEvent
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.extension.doneDate
import com.chrysalide.transmemo.presentation.extension.isValidDecimalValue
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.extension.productName
import com.chrysalide.transmemo.presentation.extension.stripTrailingZeros
import com.chrysalide.transmemo.presentation.extension.unitName
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoIntakeModal(
    viewModel: DoIntakeViewModel = koinViewModel(),
    intakeEvent: DateIntakeEvent,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.getIntakeForEvent(intakeEvent) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    when (val state = uiState) {
        DoIntakeUiState.Idle -> {}
        is DoIntakeUiState.IntakeForProduct -> {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                DoIntakeView(
                    intake = state.intake,
                    confirmIntake = viewModel::confirmIntake,
                    sheetState = sheetState,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun DoIntakeView(
    intake: Intake,
    confirmIntake: (Intake) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var editableIntake by remember(intake) { mutableStateOf(intake) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var editedDoseValue by remember { mutableStateOf(editableIntake.realDose.stripTrailingZeros()) }
    val isDoseValid = editedDoseValue.isValidDecimalValue()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Text(intake.productName(), style = MaterialTheme.typography.headlineLarge)
        Text(intake.moleculeName(), style = MaterialTheme.typography.headlineSmall)
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
            label = { Text(stringResource(string.feature_intakes_planned_date)) },
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = "Select date")
            },
            value = editableIntake.doneDate(),
            onValueChange = {},
            readOnly = true
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(string.feature_intakes_planned_dose)) },
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
            TextButton(
                onClick = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
            ) { Text(stringResource(string.global_cancel)) }
            Spacer(Modifier.width(24.dp))
            Button(
                onClick = {
                    editableIntake = editableIntake.copy(realDose = editedDoseValue.toFloat())
                    confirmIntake(editableIntake)
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
                enabled = isDoseValid
            ) { Text(stringResource(string.global_confirm)) }
        }
    }

    if (showDatePickerDialog) {
        DatePickerDialog(
            initialSelectedDate = editableIntake.realDate,
            onDateSelected = { editableIntake = editableIntake.copy(realDate = it) },
            onDismiss = { showDatePickerDialog = false }
        )
    }
}

@ExperimentalMaterial3Api
@Composable
private fun DatePickerDialog(
    initialSelectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate.toEpochMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { onDateSelected(it.toLocalDate()) }
                onDismiss()
            }) { Text(stringResource(string.global_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(string.global_cancel)) }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun DoIntakeModalViewPreview() {
    TransMemoTheme {
        DoIntakeView(
            intake = Intake(
                product = Product.default().copy(name = "Testosterone"),
                realDose = 1f,
                plannedDose = 1f,
                realDate = getCurrentLocalDate(),
                plannedDate = getCurrentLocalDate(),
                realSide = IntakeSide.LEFT,
                plannedSide = IntakeSide.LEFT
            ),
            confirmIntake = {},
            onDismiss = {}
        )
    }
}
