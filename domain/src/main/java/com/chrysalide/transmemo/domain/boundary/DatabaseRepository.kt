package com.chrysalide.transmemo.domain.boundary

import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.Note
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.domain.model.Wellbeing
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun insertContainers(containers: List<Container>)

    suspend fun insertIntakes(intakes: List<Intake>)

    suspend fun insertProducts(products: List<Product>)

    suspend fun insertWellbeings(wellbeings: List<Wellbeing>)

    suspend fun insertNotes(notes: List<Note>)

    suspend fun deleteAllData()

    fun observeAllProducts(): Flow<List<Product>>

    suspend fun getAllProducts(): List<Product>

    suspend fun updateProduct(product: Product)

    suspend fun insertProduct(product: Product)

    suspend fun deleteProduct(product: Product)

    fun observeAllContainers(): Flow<List<Container>>

    suspend fun deleteContainer(container: Container)

    suspend fun updateContainer(container: Container)

    suspend fun recycleContainer(container: Container)

    suspend fun getAllIntakes(): List<Intake>

    suspend fun getLastIntakeForProduct(productId: Int): Intake?

    suspend fun getProductContainer(productId: Int): Container
}
