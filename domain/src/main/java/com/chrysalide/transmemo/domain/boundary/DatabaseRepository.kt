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

    fun getAllProducts(): Flow<List<Product>>

    suspend fun updateProduct(product: Product)

    suspend fun insertProduct(product: Product)

    suspend fun deleteProduct(product: Product)
}
