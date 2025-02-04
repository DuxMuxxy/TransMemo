package com.chrysalide.transmemo.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.chrysalide.transmemo.core.datastore.UserPreferences
import com.chrysalide.transmemo.datastore.PreferencesDataSource
import com.chrysalide.transmemo.datastore.UserPreferencesSerializer
import com.chrysalide.transmemo.datastore.repository.AndroidUserDataRepository
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val DATA_STORE_FILE_NAME = "user_preferences.pb"

private val repositoryModule = module {
    singleOf(::AndroidUserDataRepository) bind UserDataRepository::class
}

private val coreModule = module {
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

val dataStoreModule = repositoryModule + coreModule
