package com.chrysalide.transmemo.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.ComputeNextIntakeForProductUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.presentation.history.HistoryUiState.Loading
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class HistoryViewModel(
    databaseRepository: DatabaseRepository,
    computeNextIntakeForProductsUseCase: ComputeNextIntakeForProductUseCase
) : ViewModel() {
    val uiState: StateFlow<HistoryUiState> = databaseRepository
        .observeAllIntakes()
        .map { intakes ->
            if (intakes.isNotEmpty()) {
                val products = intakes.map { it.product }.distinct()
                HistoryUiState.History(
                    nextIntakes = computeNextIntakeForProductsUseCase(products),
                    intakes = intakes
                )
            } else {
                HistoryUiState.Empty
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = Loading
        )
}

sealed interface HistoryUiState {
    data object Loading : HistoryUiState

    data object Empty : HistoryUiState

    data class History(
        val nextIntakes: List<Intake>,
        val intakes: List<Intake>
    ) : HistoryUiState
}
