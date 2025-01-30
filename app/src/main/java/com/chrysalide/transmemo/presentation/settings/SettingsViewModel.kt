package com.chrysalide.transmemo.presentation.settings

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.core.model.DarkThemeConfig
import com.chrysalide.transmemo.core.repository.UserDataRepository
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.Loading
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.UserEditableSettings
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SettingsViewModel(
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    val settingsUiState: StateFlow<SettingsUiState> = userDataRepository.userData
        .map { userData ->
            UserEditableSettings(darkThemeConfig = userData.darkThemeConfig)
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

    fun importDatabaseFile(fileUri: Uri) {
        Log.d("TEST", "importDatabaseFile: $fileUri")
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class UserEditableSettings(
        val darkThemeConfig: DarkThemeConfig
    ) : SettingsUiState
}
