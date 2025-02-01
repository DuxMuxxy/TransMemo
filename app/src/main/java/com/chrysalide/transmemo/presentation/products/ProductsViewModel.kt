package com.chrysalide.transmemo.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.core.model.entities.ProductEntity
import com.chrysalide.transmemo.core.repository.DatabaseRepository
import com.chrysalide.transmemo.presentation.products.ProductsUiState.Products
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class ProductsViewModel(
    databaseRepository: DatabaseRepository
) : ViewModel() {
    val productsUiState: StateFlow<ProductsUiState> = databaseRepository
        .getAllProducts()
        .map(::Products)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = ProductsUiState.Loading
        )
}

sealed interface ProductsUiState {
    data object Loading : ProductsUiState

    data class Products(
        val products: List<ProductEntity>
    ) : ProductsUiState
}
