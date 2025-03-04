package com.chrysalide.transmemo.presentation.products.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.DoIntakeForProductUseCase
import com.chrysalide.transmemo.data.usecase.ScheduleAlertsForProductUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.minus

class AddProductViewModel(
    private val databaseRepository: DatabaseRepository,
    private val scheduleAlertsForProductUseCase: ScheduleAlertsForProductUseCase,
    private val doIntakeForProductUseCase: DoIntakeForProductUseCase
) : ViewModel() {
    private var product: Product = Product.default()

    private val _uiState = MutableStateFlow<AddProductUiState>(AddProductUiState.ProductDetails(product))
    val uiState: StateFlow<AddProductUiState> = _uiState

    fun addProduct(product: Product) {
        viewModelScope.launch {
            this@AddProductViewModel.product = product
            val newIntake = Intake(
                product = product,
                realDose = product.dosePerIntake,
                plannedDose = product.dosePerIntake,
                realDate = getCurrentLocalDate(),
                plannedDate = getCurrentLocalDate(),
                realSide = product.initIntakeSide(),
                plannedSide = product.initIntakeSide()
            )
            _uiState.update { AddProductUiState.SaveIntake(newIntake) }
        }
    }

    fun backToProductDetails() {
        _uiState.update { AddProductUiState.ProductDetails(product) }
    }

    fun saveIntake(intake: Intake) {
        viewModelScope.launch {
            val productId = databaseRepository.insertProduct(product)
            // We update the product id with the generated id
            product = product.copy(id = productId)

            if (intake.realDate < getCurrentLocalDate()) {
                insertPastIntake(intake)
            } else {
                scheduleIntake(intake)
            }

            // Reset product after saving
            product = Product.default()
            _uiState.update { AddProductUiState.ProductDetails(product) }
        }
    }

    private suspend fun insertPastIntake(intake: Intake) {
        doIntakeForProductUseCase(intake.copy(product = product))
        scheduleAlertsForProductUseCase(product)
    }

    private suspend fun scheduleIntake(intake: Intake) {
        val date = intake.realDate.minus(DatePeriod(days = intake.product.intakeInterval))
        val side = intake.realSide.getNextSide()
        doIntakeForProductUseCase(
            intake.copy(
                product = product,
                realDate = date,
                plannedDate = date,
                realDose = 0f,
                plannedDose = 0f,
                realSide = side,
                plannedSide = side,
                forScheduledIntake = true
            )
        )
        scheduleAlertsForProductUseCase(product)
    }
}

sealed interface AddProductUiState {
    data class ProductDetails(
        val product: Product
    ) : AddProductUiState

    data class SaveIntake(
        val intake: Intake
    ) : AddProductUiState
}
