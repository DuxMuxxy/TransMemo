package com.chrysalide.transmemo.presentation.products.delete

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
            Text(
                text = stringResource(string.global_confirm),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable(onClick = onConfirm)
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
private fun Preview() {
    TransMemoTheme {
        AskDeleteProductDialog({}, {})
    }
}
