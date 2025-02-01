package com.chrysalide.transmemo.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        ContainerEntity::class,
        NoteEntity::class,
        ProductEntity::class,
        TakeEntity::class,
        WellnessEntity::class,
    ],
    version = 1
)
abstract class TransMemoDatabase : RoomDatabase() {
    abstract fun containerDao(): ContainerDao

    abstract fun noteDao(): NoteDao

    abstract fun productDao(): ProductDao

    abstract fun takeDao(): TakeDao

    abstract fun wellnessDao(): WellnessDao
}
