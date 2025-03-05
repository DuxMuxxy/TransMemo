package com.chrysalide.transmemo.presentation.settings

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.AndroidBiometricRepository
import com.chrysalide.transmemo.data.usecase.ScheduleAllAlertsUseCase
import com.chrysalide.transmemo.database.usecase.ImportOldDatabaseUseCase
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import com.chrysalide.transmemo.domain.model.DarkThemeConfig
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.Loading
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.UserEditableSettings
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SettingsViewModel(
    private val userDataRepository: UserDataRepository,
    private val importOldDatabaseUseCase: ImportOldDatabaseUseCase,
    private val scheduleAllAlertsUseCase: ScheduleAllAlertsUseCase,
    private val biometricRepository: AndroidBiometricRepository
) : ViewModel() {
    var importResultSnackbar by mutableStateOf<ImportDatabaseFileResultSnackbar>(ImportDatabaseFileResultSnackbar.Idle)

    val uiState: StateFlow<SettingsUiState> = userDataRepository.userData
        .map { userData ->
            UserEditableSettings(
                darkThemeConfig = userData.darkThemeConfig,
                useDynamicColor = userData.useDynamicColor,
                canDeviceAskAuthentication = biometricRepository.canDeviceAskAuthentication(),
                askAuthentication = userData.askAuthentication,
                useAlternativeAppIconAndName = userData.useAlternativeAppIconAndName,
                useCustomNotificationMessage = userData.useCustomNotificationMessage,
                customNotificationMessage = userData.customNotificationMessage
            )
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = Loading,
        )

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userDataRepository.setDynamicColorPreference(useDynamicColor)
        }
    }

    fun updateAskAuthentication(askAuthentication: Boolean) {
        viewModelScope.launch {
            userDataRepository.setAskAuthentication(askAuthentication)
        }
    }

    fun updateUseAlternativeAppIconAndName(useAlternativeAppIconAndName: Boolean) {
        viewModelScope.launch {
            userDataRepository.setUseAlternativeAppIconAndName(useAlternativeAppIconAndName)
        }
    }

    fun updateUseCustomNotificationMessage(useCustomNotificationMessage: Boolean) {
        viewModelScope.launch {
            userDataRepository.setUseCustomNotificationMessage(useCustomNotificationMessage)
        }
    }

    fun updateCustomNotificationMessage(customNotificationMessage: String) {
        viewModelScope.launch {
            userDataRepository.setCustomNotificationMessage(customNotificationMessage)
        }
    }

    fun importDatabaseFile(fileUri: Uri) {
        viewModelScope.launch {
            importOldDatabaseUseCase(
                dbFileUri = fileUri,
                onSuccess = {
                    importResultSnackbar = ImportDatabaseFileResultSnackbar.Success
                    viewModelScope.launch { scheduleAllAlertsUseCase() }
                },
                onError = {
                    importResultSnackbar = ImportDatabaseFileResultSnackbar.Error
                }
            )
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class UserEditableSettings(
        val darkThemeConfig: DarkThemeConfig,
        val useDynamicColor: Boolean,
        val canDeviceAskAuthentication: Boolean,
        val askAuthentication: Boolean,
        val useAlternativeAppIconAndName: Boolean,
        val useCustomNotificationMessage: Boolean,
        val customNotificationMessage: String
    ) : SettingsUiState
}

sealed interface ImportDatabaseFileResultSnackbar {
    data object Idle : ImportDatabaseFileResultSnackbar

    data object Success : ImportDatabaseFileResultSnackbar

    data object Error : ImportDatabaseFileResultSnackbar
}
