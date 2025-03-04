package com.chrysalide.transmemo.presentation.design

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.domain.extension.toLocalDate
import com.chrysalide.transmemo.domain.extension.toMidDayEpochMillis
import kotlinx.datetime.LocalDate

@ExperimentalMaterial3Api
@Composable
fun DatePickerDialog(
    initialSelectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDate.toMidDayEpochMillis()
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { onDateSelected(it.toLocalDate()) }
                onDismiss()
            }) { Text(stringResource(R.string.global_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.global_cancel)) }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
