package com.chrysalide.transmemo.presentation.products.add

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Product
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Test

class AddProductViewModelTest {
    private val databaseRepository: DatabaseRepository = mockk(relaxed = true)
    private val viewModel = AddProductViewModel(databaseRepository)

    @Test
    fun insertProductWhenAddProductInvoke() {
        // Arrange
        val product = Product.default()

        // Act
        viewModel.addProduct(product)

        // Assert
        coVerify { databaseRepository.insertProduct(product) }
    }
}
