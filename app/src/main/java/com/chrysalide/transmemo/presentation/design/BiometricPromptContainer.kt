package com.chrysalide.transmemo.presentation.design

import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.presentation.MainActivity

@Composable
fun BiometricPromptContainer(
    onAuthSucceeded: () -> Unit = {},
    onAuthError: () -> Unit = {}
) {
    val context = LocalContext.current
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val callback = remember {
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onAuthError()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onAuthSucceeded()
            }

            override fun onAuthenticationFailed() {
                onAuthError()
            }
        }
    }

    val promptTitle = stringResource(R.string.biometric_prompt_title)
    val promptSubtitle = stringResource(R.string.biometric_prompt_subtitle)
    val prompt = BiometricPrompt(context as MainActivity, executor, callback)
    val promptInfo = PromptInfo
        .Builder()
        .setTitle(promptTitle)
        .setSubtitle(promptSubtitle)
        .setAllowedAuthenticators(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
        .build()
    prompt.authenticate(promptInfo)
}
