package com.chrysalide.transmemo.presentation.intakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.ComputeNextIntakeForProductUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Intake
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IntakesViewModel(
    databaseRepository: DatabaseRepository,
    computeNextIntakeForProductUseCase: ComputeNextIntakeForProductUseCase
) : ViewModel() {
    private val _intakesUiState = MutableStateFlow<IntakesUiState>(IntakesUiState.Loading)
    val intakesUiState: StateFlow<IntakesUiState> = _intakesUiState

    init {
        viewModelScope.launch {
            val previousIntakes = databaseRepository.getAllIntakes()
            val product = previousIntakes.first().product
            _intakesUiState.update {
                IntakesUiState.Intakes(
                    nextIntake = computeNextIntakeForProductUseCase(product),
                    intakes = previousIntakes
                )
            }
        }
    }
}

sealed interface IntakesUiState {
    data object Loading : IntakesUiState

    data class Intakes(
        val nextIntake: Intake,
        val intakes: List<Intake>
    ) : IntakesUiState
}
