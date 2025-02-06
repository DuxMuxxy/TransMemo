package com.chrysalide.transmemo.presentation.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chrysalide.transmemo.presentation.design.BiometricPromptContainer

@Composable
fun AuthScreen(onAuthSucceeded: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        BiometricPromptContainer(onAuthSucceeded)
    }
}
