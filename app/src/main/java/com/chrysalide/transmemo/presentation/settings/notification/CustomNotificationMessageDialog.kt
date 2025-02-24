package com.chrysalide.transmemo.presentation.settings.notification

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun CustomNotificationMessageDialog(
    customMessage: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var customNotificationMessage by remember {
        mutableStateOf(
            TextFieldValue(text = customMessage, selection = TextRange(customMessage.length))
        )
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(string.feature_settings_custom_notification_dialog_title)) },
        text = {
            OutlinedTextField(
                value = customNotificationMessage,
                onValueChange = { customNotificationMessage = it },
                modifier = Modifier.focusRequester(focusRequester)
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(customNotificationMessage.text) },
                enabled = customNotificationMessage.text.isNotEmpty()
            ) {
                Text(text = stringResource(string.global_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(string.global_cancel))
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@ThemePreviews
@Composable
private fun CustomNotificationMessageDialogPreview() {
    TransMemoTheme {
        CustomNotificationMessageDialog("Water the plants", {}, {})
    }
}
