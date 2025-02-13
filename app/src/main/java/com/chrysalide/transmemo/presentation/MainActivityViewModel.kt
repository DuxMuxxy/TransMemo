package com.chrysalide.transmemo.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.database.usecase.AutoImportOldDatabaseUseCase
import com.chrysalide.transmemo.domain.boundary.BiometricRepository
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import com.chrysalide.transmemo.domain.model.DarkThemeConfig
import com.chrysalide.transmemo.presentation.MainActivityUiState.Loading
import com.chrysalide.transmemo.presentation.MainActivityUiState.Success
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivityViewModel(
    userDataRepository: UserDataRepository,
    autoImportOldDatabaseUseCase: AutoImportOldDatabaseUseCase,
    biometricRepository: BiometricRepository
) : ViewModel() {
    var shouldAskAuthenticationAtLaunch by mutableStateOf(false)

    init {
        viewModelScope.launch {
            autoImportOldDatabaseUseCase()
            shouldAskAuthenticationAtLaunch =
                userDataRepository.userData.first().askAuthentication &&
                biometricRepository.canDeviceAskAuthentication()
        }
    }

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData
        .map {
            Success(it.darkThemeConfig, it.useDynamicColor)
        }.stateIn(
            scope = viewModelScope,
            initialValue = Loading,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
        )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(
        val darkThemeConfig: DarkThemeConfig,
        val useDynamicColor: Boolean = false
    ) : MainActivityUiState
}
