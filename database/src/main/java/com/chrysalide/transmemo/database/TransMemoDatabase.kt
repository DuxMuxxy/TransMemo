package com.chrysalide.transmemo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chrysalide.transmemo.database.dao.ContainerDao
import com.chrysalide.transmemo.database.dao.IntakeDao
import com.chrysalide.transmemo.database.dao.NoteDao
import com.chrysalide.transmemo.database.dao.ProductDao
import com.chrysalide.transmemo.database.dao.WellbeingDao
import com.chrysalide.transmemo.database.entity.ContainerDBEntity
import com.chrysalide.transmemo.database.entity.IntakeDBEntity
import com.chrysalide.transmemo.database.entity.NoteDBEntity
import com.chrysalide.transmemo.database.entity.ProductDBEntity
import com.chrysalide.transmemo.database.entity.WellbeingDBEntity

@Database(
    entities = [
        ContainerDBEntity::class,
        NoteDBEntity::class,
        ProductDBEntity::class,
        IntakeDBEntity::class,
        WellbeingDBEntity::class,
    ],
    version = 1
)
abstract class TransMemoDatabase : RoomDatabase() {
    abstract fun containerDao(): ContainerDao

    abstract fun noteDao(): NoteDao

    abstract fun productDao(): ProductDao

    abstract fun takeDao(): IntakeDao

    abstract fun wellnessDao(): WellbeingDao
}
