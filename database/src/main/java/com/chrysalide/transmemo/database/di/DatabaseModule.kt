package com.chrysalide.transmemo.database.di

import androidx.room.Room
import com.chrysalide.transmemo.database.MIGRATION_1_2
import com.chrysalide.transmemo.database.TransMemoDatabase
import com.chrysalide.transmemo.database.dao.ContainerDao
import com.chrysalide.transmemo.database.dao.IntakeDao
import com.chrysalide.transmemo.database.dao.NoteDao
import com.chrysalide.transmemo.database.dao.ProductDao
import com.chrysalide.transmemo.database.dao.WellbeingDao
import com.chrysalide.transmemo.database.helper.ImportDatabaseHelper
import com.chrysalide.transmemo.database.repository.RoomDatabaseRepository
import com.chrysalide.transmemo.database.usecase.AutoImportOldDatabaseUseCase
import com.chrysalide.transmemo.database.usecase.ImportOldDatabaseUseCase
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

const val DATABASE_NAME = "transmemo.db"

private val repositoryModule = module {
    singleOf(::RoomDatabaseRepository) bind DatabaseRepository::class
}

private val daoModule = module {
    single<ContainerDao> { get<TransMemoDatabase>().containerDao() }
    single<NoteDao> { get<TransMemoDatabase>().noteDao() }
    single<ProductDao> { get<TransMemoDatabase>().productDao() }
    single<IntakeDao> { get<TransMemoDatabase>().intakeDao() }
    single<WellbeingDao> { get<TransMemoDatabase>().wellbeingDao() }
}

private val helperModule = module {
    singleOf(::ImportDatabaseHelper)
}

private val dbModule = module {
    single<TransMemoDatabase> {
        Room
            .databaseBuilder(androidContext(), TransMemoDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}

private val useCaseModule = module {
    singleOf(::ImportOldDatabaseUseCase)
    singleOf(::AutoImportOldDatabaseUseCase)
}

val databaseModule = repositoryModule + daoModule + helperModule + dbModule + useCaseModule
