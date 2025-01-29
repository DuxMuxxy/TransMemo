package com.chrysalide.transmemo.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.chrysalide.transmemo.core.datastore.PreferencesDataSource
import com.chrysalide.transmemo.core.datastore.UserPreferences
import com.chrysalide.transmemo.core.datastore.UserPreferencesSerializer
import com.chrysalide.transmemo.core.repository.TransMemoUserDataRepository
import com.chrysalide.transmemo.core.repository.UserDataRepository
import com.chrysalide.transmemo.presentation.MainActivityViewModel
import com.chrysalide.transmemo.presentation.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private const val DATA_STORE_FILE_NAME = "user_preferences.pb"

private val coreModule = module {
    singleOf(::TransMemoUserDataRepository) { bind<UserDataRepository>() }
}

private val dataStoreModule = module {
    singleOf(::PreferencesDataSource)
    singleOf(::UserPreferencesSerializer)
    single<DataStore<UserPreferences>> {
        DataStoreFactory.create(
            serializer = get<UserPreferencesSerializer>(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ) {
            androidContext().dataStoreFile(DATA_STORE_FILE_NAME)
        }
    }
}

private val viewModelModule = module {
    viewModelOf(::MainActivityViewModel)
    viewModelOf(::SettingsViewModel)
}

val appModule = coreModule + dataStoreModule + viewModelModule
