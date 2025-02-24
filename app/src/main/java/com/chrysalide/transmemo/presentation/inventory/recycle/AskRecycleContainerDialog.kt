package com.chrysalide.transmemo.presentation.inventory.recycle

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AskRecycleContainerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.feature_inventory_recycle_dialog_title)) },
        text = { Text(stringResource(R.string.feature_inventory_recycle_dialog_text)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(R.string.global_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.global_cancel))
            }
        }
    )
}

@ThemePreviews
@Composable
private fun AskRecycleContainerDialogPreview() {
    TransMemoTheme {
        AskRecycleContainerDialog({}, {})
    }
}
