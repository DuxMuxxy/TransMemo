package com.chrysalide.transmemo.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.chrysalide.transmemo.core.database.ImportDatabaseHelper
import com.chrysalide.transmemo.core.database.TransMemoDatabase
import com.chrysalide.transmemo.core.database.dao.ContainerDao
import com.chrysalide.transmemo.core.database.dao.NoteDao
import com.chrysalide.transmemo.core.database.dao.ProductDao
import com.chrysalide.transmemo.core.database.dao.TakeDao
import com.chrysalide.transmemo.core.database.dao.WellnessDao
import com.chrysalide.transmemo.core.datastore.PreferencesDataSource
import com.chrysalide.transmemo.core.datastore.UserPreferences
import com.chrysalide.transmemo.core.datastore.UserPreferencesSerializer
import com.chrysalide.transmemo.core.repository.DatabaseRepository
import com.chrysalide.transmemo.core.repository.UserDataRepository
import com.chrysalide.transmemo.core.usecase.AutoImportOldDatabaseUseCase
import com.chrysalide.transmemo.core.usecase.ImportOldDatabaseUseCase
import com.chrysalide.transmemo.presentation.MainActivityViewModel
import com.chrysalide.transmemo.presentation.calendar.CalendarViewModel
import com.chrysalide.transmemo.presentation.products.ProductsViewModel
import com.chrysalide.transmemo.presentation.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

const val DATABASE_NAME = "transmemo"
private const val DATA_STORE_FILE_NAME = "user_preferences.pb"

private val coreModule = module {
    singleOf(::UserDataRepository)
    singleOf(::DatabaseRepository)
}

private val useCaseModule = module {
    singleOf(::ImportOldDatabaseUseCase)
    singleOf(::AutoImportOldDatabaseUseCase)
}

private val dataStoreModule = module {
    singleOf(::PreferencesDataSource)
    singleOf(::UserPreferencesSerializer)
    single<DataStore<UserPreferences>> {
        DataStoreFactory.create(
            serializer = get<UserPreferencesSerializer>(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        ) {
            androidContext().dataStoreFile(DATA_STORE_FILE_NAME)
        }
    }
}

private val viewModelModule = module {
    viewModelOf(::MainActivityViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::ProductsViewModel)
    viewModelOf(::SettingsViewModel)
}

private val databaseModule = module {
    single<TransMemoDatabase> {
        Room
            .databaseBuilder(androidContext(), TransMemoDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
    single<ContainerDao> { get<TransMemoDatabase>().containerDao() }
    single<NoteDao> { get<TransMemoDatabase>().noteDao() }
    single<ProductDao> { get<TransMemoDatabase>().productDao() }
    single<TakeDao> { get<TransMemoDatabase>().takeDao() }
    single<WellnessDao> { get<TransMemoDatabase>().wellnessDao() }
    singleOf(::ImportDatabaseHelper)
}

val appModule = coreModule + useCaseModule + dataStoreModule + viewModelModule + databaseModule
