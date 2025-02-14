package com.chrysalide.transmemo.presentation.intakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.ComputeNextIntakeForProductUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.presentation.intakes.IntakesUiState.Loading
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class IntakesViewModel(
    databaseRepository: DatabaseRepository,
    computeNextIntakeForProductsUseCase: ComputeNextIntakeForProductUseCase
) : ViewModel() {
    val intakesUiState: StateFlow<IntakesUiState> = databaseRepository
        .observeAllIntakes()
        .map { intakes ->
            if (intakes.isNotEmpty()) {
                val products = intakes.map { it.product }.distinct()
                IntakesUiState.Intakes(
                    nextIntakes = computeNextIntakeForProductsUseCase(products),
                    intakes = intakes
                )
            } else {
                IntakesUiState.Empty
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = Loading
        )
}

sealed interface IntakesUiState {
    data object Loading : IntakesUiState

    data object Empty : IntakesUiState

    data class Intakes(
        val nextIntakes: List<Intake>,
        val intakes: List<Intake>
    ) : IntakesUiState
}
