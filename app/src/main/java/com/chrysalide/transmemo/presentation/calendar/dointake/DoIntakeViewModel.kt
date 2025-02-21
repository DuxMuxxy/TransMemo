package com.chrysalide.transmemo.presentation.calendar.dointake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.CreateIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.DoIntakeForProductUseCase
import com.chrysalide.transmemo.domain.model.DateIntakeEvent
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.presentation.notification.IntakeAlertNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DoIntakeViewModel(
    private val createIntakeForProductUseCase: CreateIntakeForProductUseCase,
    private val doIntakeForProductUseCase: DoIntakeForProductUseCase,
    private val intakeAlertNotifier: IntakeAlertNotifier
) : ViewModel() {
    private val _uiState = MutableStateFlow<DoIntakeUiState>(DoIntakeUiState.Idle)
    val uiState = _uiState

    fun getIntakeForEvent(dateIntakeEvent: DateIntakeEvent) {
        viewModelScope.launch {
            val intake = createIntakeForProductUseCase(dateIntakeEvent)
            _uiState.update { DoIntakeUiState.IntakeForProduct(intake) }
        }
    }

    fun confirmIntake(intake: Intake) {
        viewModelScope.launch {
            doIntakeForProductUseCase(intake)
            intakeAlertNotifier.cancelNotification(intake.product.id)
        }
    }
}

sealed interface DoIntakeUiState {
    data object Idle : DoIntakeUiState

    data class IntakeForProduct(
        val intake: Intake
    ) : DoIntakeUiState
}
