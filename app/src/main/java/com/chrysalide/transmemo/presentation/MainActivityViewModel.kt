package com.chrysalide.transmemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.core.usecase.AutoImportOldDatabaseUseCase
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import com.chrysalide.transmemo.domain.model.UserData
import com.chrysalide.transmemo.presentation.MainActivityUiState.Loading
import com.chrysalide.transmemo.presentation.MainActivityUiState.Success
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivityViewModel(
    userDataRepository: UserDataRepository,
    autoImportOldDatabaseUseCase: AutoImportOldDatabaseUseCase
) : ViewModel() {
    init {
        viewModelScope.launch {
            autoImportOldDatabaseUseCase()
        }
    }

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData
        .map {
            Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = Loading,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds)
        )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(
        val userData: UserData
    ) : MainActivityUiState
}
