package com.chrysalide.transmemo.presentation.products.delete

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AskDeleteProductDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(string.feature_products_delete_dialog_title)) },
        text = { Text(stringResource(string.feature_products_delete_dialog_text)) },
        confirmButton = {
            Button(onClick = onConfirm) {
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
private fun AskDeleteProductDialogPreview() {
    TransMemoTheme {
        AskDeleteProductDialog({}, {})
    }
}
