package com.chrysalide.transmemo.presentation.calendar.dointake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.CreateIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.DoIntakeForProductUseCase
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DoIntakeViewModel(
    private val createIntakeForProductUseCase: CreateIntakeForProductUseCase,
    private val doIntakeForProductUseCase: DoIntakeForProductUseCase
) : ViewModel() {
    private val _doIntakeUiState = MutableStateFlow<DoIntakeUiState>(DoIntakeUiState.Idle)
    val doIntakeUiState = _doIntakeUiState

    fun getIntakeForProduct(product: Product) {
        viewModelScope.launch {
            val intake = createIntakeForProductUseCase(product)
            _doIntakeUiState.update { DoIntakeUiState.IntakeForProduct(intake) }
        }
    }

    fun confirmIntake(intake: Intake) {
        viewModelScope.launch {
            doIntakeForProductUseCase(intake)
        }
    }
}

sealed interface DoIntakeUiState {
    data object Idle : DoIntakeUiState

    data class IntakeForProduct(
        val intake: Intake
    ) : DoIntakeUiState
}
