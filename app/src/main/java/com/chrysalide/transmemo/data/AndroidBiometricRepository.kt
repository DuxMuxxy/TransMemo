package com.chrysalide.transmemo.data

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import com.chrysalide.transmemo.domain.boundary.BiometricRepository

class AndroidBiometricRepository(
    private val biometricManager: BiometricManager
) : BiometricRepository {
    override fun canDeviceAskAuthentication() =
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
}
