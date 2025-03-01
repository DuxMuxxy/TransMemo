package com.chrysalide.transmemo.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.ScheduleAlertsForProductUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.products.ProductsUiState.Products
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ProductsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val scheduleAlertsForProductUseCase: ScheduleAlertsForProductUseCase
) : ViewModel() {
    val productsUiState: StateFlow<ProductsUiState> = databaseRepository
        .observeAllProducts()
        .map {
            if (it.isNotEmpty()) Products(it) else ProductsUiState.Empty
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = ProductsUiState.Loading
        )

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            databaseRepository.updateProduct(product)
            scheduleAlertsForProductUseCase(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            scheduleAlertsForProductUseCase(product.copy(inUse = false))
            databaseRepository.deleteProduct(product)
        }
    }
}

sealed interface ProductsUiState {
    data object Loading : ProductsUiState

    data object Empty : ProductsUiState

    data class Products(
        val products: List<Product>
    ) : ProductsUiState
}
