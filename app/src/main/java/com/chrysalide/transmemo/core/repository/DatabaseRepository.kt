package com.chrysalide.transmemo.core.repository

import com.chrysalide.transmemo.core.database.dao.ContainerDao
import com.chrysalide.transmemo.core.database.dao.NoteDao
import com.chrysalide.transmemo.core.database.dao.ProductDao
import com.chrysalide.transmemo.core.database.dao.TakeDao
import com.chrysalide.transmemo.core.database.dao.WellnessDao

class DatabaseRepository(
    private val containerDao: ContainerDao,
    private val noteDao: NoteDao,
    private val productDao: ProductDao,
    private val takeDao: TakeDao,
    private val wellnessDao: WellnessDao
) {

    suspend fun getAllProducts() = productDao.getAll()
}
