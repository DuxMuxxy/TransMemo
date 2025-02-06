package com.chrysalide.transmemo.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.presentation.design.BiometricPromptContainer
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AuthScreen(
    onAuthSucceeded: () -> Unit,
    showBiometricPromptAtLaunch: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        var showBiometricPrompt by remember { mutableStateOf(showBiometricPromptAtLaunch) }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(160.dp))
            Spacer(modifier = Modifier.size(32.dp))
            OutlinedButton(onClick = { showBiometricPrompt = true }) {
                Text(
                    stringResource(R.string.feature_authentication_unlock_button),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
        if (showBiometricPrompt) {
            BiometricPromptContainer(onAuthSucceeded)
            showBiometricPrompt = false
        }
    }
}

@ThemePreviews
@Composable
private fun AuthScreenPreviews() {
    TransMemoTheme {
        AuthScreen(
            onAuthSucceeded = {},
            showBiometricPromptAtLaunch = false
        )
    }
}
