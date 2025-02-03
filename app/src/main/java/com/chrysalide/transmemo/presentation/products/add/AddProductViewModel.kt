package com.chrysalide.transmemo.presentation.products.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.core.model.entities.ProductEntity
import com.chrysalide.transmemo.core.repository.DatabaseRepository
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val databaseRepository: DatabaseRepository
): ViewModel() {

    fun addProduct(productEntity: ProductEntity) {
        viewModelScope.launch {
            databaseRepository.insertProduct(productEntity)
        }
    }
}
