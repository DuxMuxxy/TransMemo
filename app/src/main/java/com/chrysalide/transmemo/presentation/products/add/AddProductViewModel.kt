package com.chrysalide.transmemo.presentation.products.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.ScheduleAlertsForProductUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Product
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val databaseRepository: DatabaseRepository,
    private val scheduleAlertsForProductUseCase: ScheduleAlertsForProductUseCase
) : ViewModel() {
    fun addProduct(product: Product) {
        viewModelScope.launch {
            databaseRepository.insertProduct(product)
            scheduleAlertsForProductUseCase(product)
        }
    }
}
