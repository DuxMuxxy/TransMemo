package com.chrysalide.transmemo.core.repository

import com.chrysalide.transmemo.core.datastore.PreferencesDataSource
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import com.chrysalide.transmemo.domain.model.DarkThemeConfig
import com.chrysalide.transmemo.domain.model.UserData
import kotlinx.coroutines.flow.Flow

class AndroidUserDataRepository(
    private val preferencesDataSource: PreferencesDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> = preferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setLegacyDatabaseHasBeenImported() {
        preferencesDataSource.setLegacyDatabaseHasBeenImported()
    }
}
