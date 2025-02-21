package com.chrysalide.transmemo.presentation.calendar.ignore

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AskIgnoreIntakeDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(string.feature_calendar_ignore_dialog_title)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(string.global_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(string.global_cancel))
            }
        }
    )
}

@ThemePreviews
@Composable
private fun AskIgnoreIntakeDialogPreview() {
    TransMemoTheme {
        AskIgnoreIntakeDialog({}, {})
    }
}
