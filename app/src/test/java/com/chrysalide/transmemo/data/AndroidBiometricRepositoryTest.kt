package com.chrysalide.transmemo.data

import androidx.biometric.BiometricManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AndroidBiometricRepositoryTest {
    private val biometricManager: BiometricManager = mockk()
    private val biometricRepository = AndroidBiometricRepository(biometricManager)

    @Test
    fun returnTrueWhenBiometricIsAvailable() {
        // Arrange
        every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_SUCCESS

        // Act
        val result = biometricRepository.canDeviceAskAuthentication()

        // Assert
        assertTrue(result)
    }

    @Test
    fun returnFalseWhenBiometricIsUnsupported() {
        // Arrange
        every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED

        // Act
        val result = biometricRepository.canDeviceAskAuthentication()

        // Assert
        assertFalse(result)
    }

    @Test
    fun returnFalseWhenNoBiometricHardware() {
        // Arrange
        every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE

        // Act
        val result = biometricRepository.canDeviceAskAuthentication()

        // Assert
        assertFalse(result)
    }

    @Test
    fun returnFalseWhenStatusUnknown() {
        // Arrange
        every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_STATUS_UNKNOWN

        // Act
        val result = biometricRepository.canDeviceAskAuthentication()

        // Assert
        assertFalse(result)
    }

    @Test
    fun returnFalseWhenBiometricHardwareUnavailable() {
        // Arrange
        every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE

        // Act
        val result = biometricRepository.canDeviceAskAuthentication()

        // Assert
        assertFalse(result)
    }

    @Test
    fun returnFalseWhenSecurityUpdateRequired() {
        // Arrange
        every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED

        // Act
        val result = biometricRepository.canDeviceAskAuthentication()

        // Assert
        assertFalse(result)
    }

    @Test
    fun returnFalseWhenNoBiometricEnrolled() {
        // Arrange
        every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        // Act
        val result = biometricRepository.canDeviceAskAuthentication()

        // Assert
        assertFalse(result)
    }
}
