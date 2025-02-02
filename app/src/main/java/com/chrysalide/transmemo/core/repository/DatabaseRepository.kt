package com.chrysalide.transmemo.core.repository

import com.chrysalide.transmemo.core.database.dao.ContainerDao
import com.chrysalide.transmemo.core.database.dao.NoteDao
import com.chrysalide.transmemo.core.database.dao.ProductDao
import com.chrysalide.transmemo.core.database.dao.TakeDao
import com.chrysalide.transmemo.core.database.dao.WellnessDao
import com.chrysalide.transmemo.core.model.entities.ContainerEntity
import com.chrysalide.transmemo.core.model.entities.NoteEntity
import com.chrysalide.transmemo.core.model.entities.ProductEntity
import com.chrysalide.transmemo.core.model.entities.TakeEntity
import com.chrysalide.transmemo.core.model.entities.WellnessEntity
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val containerDao: ContainerDao,
    private val noteDao: NoteDao,
    private val productDao: ProductDao,
    private val takeDao: TakeDao,
    private val wellnessDao: WellnessDao
) {
    suspend fun insertContainers(containers: List<ContainerEntity>) = containerDao.insertAll(containers)

    suspend fun insertTakes(takes: List<TakeEntity>) = takeDao.insertAll(takes)

    suspend fun insertProducts(products: List<ProductEntity>) = productDao.insertAll(products)

    suspend fun insertWellnesses(wellnesses: List<WellnessEntity>) = wellnessDao.insertAll(wellnesses)

    suspend fun insertNotes(notes: List<NoteEntity>) = noteDao.insertAll(notes)

    suspend fun deleteAllData() {
        containerDao.deleteAll()
        noteDao.deleteAll()
        productDao.deleteAll()
        takeDao.deleteAll()
        wellnessDao.deleteAll()
    }

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAll()

    suspend fun updateProduct(productEntity: ProductEntity) = productDao.update(productEntity)
}
