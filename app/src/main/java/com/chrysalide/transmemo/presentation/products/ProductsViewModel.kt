package com.chrysalide.transmemo.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val productsUiState: StateFlow<ProductsUiState> = databaseRepository
        .getAllProducts()
        .map(::Products)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = ProductsUiState.Loading
        )

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            databaseRepository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            databaseRepository.deleteProduct(product)
        }
    }
}

sealed interface ProductsUiState {
    data object Loading : ProductsUiState

    data class Products(
        val products: List<Product>
    ) : ProductsUiState
}
