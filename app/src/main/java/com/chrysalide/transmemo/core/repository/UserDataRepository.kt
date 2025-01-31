package com.chrysalide.transmemo.core.repository

import com.chrysalide.transmemo.core.datastore.PreferencesDataSource
import com.chrysalide.transmemo.core.model.DarkThemeConfig
import com.chrysalide.transmemo.core.model.UserData
import kotlinx.coroutines.flow.Flow

class UserDataRepository(
    private val preferencesDataSource: PreferencesDataSource
) {
    val userData: Flow<UserData> = preferencesDataSource.userData

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    suspend fun setLegacyDatabaseHasBeenImported() {
        preferencesDataSource.setLegacyDatabaseHasBeenImported()
    }
}
