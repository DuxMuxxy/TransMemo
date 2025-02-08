package com.chrysalide.transmemo.datastore.repository

import com.chrysalide.transmemo.datastore.PreferencesDataSource
import com.chrysalide.transmemo.domain.boundary.UserDataRepository
import com.chrysalide.transmemo.domain.model.DarkThemeConfig
import com.chrysalide.transmemo.domain.model.UserData
import kotlinx.coroutines.flow.Flow

internal class AndroidUserDataRepository(
    private val preferencesDataSource: PreferencesDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> = preferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setLegacyDatabaseHasBeenImported() {
        preferencesDataSource.setLegacyDatabaseHasBeenImported()
    }

    override suspend fun setAskAuthentication(askAuthentication: Boolean) {
        preferencesDataSource.setAskAuthentication(askAuthentication)
    }

    override suspend fun setUseAlternativeAppIconAndName(useAlternativeAppIconAndName: Boolean) {
        preferencesDataSource.setUseAlternativeAppIconAndName(useAlternativeAppIconAndName)
    }
}
