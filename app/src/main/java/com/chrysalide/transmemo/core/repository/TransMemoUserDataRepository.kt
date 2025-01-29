package com.chrysalide.transmemo.core.repository

import com.chrysalide.transmemo.core.datastore.PreferencesDataSource
import com.chrysalide.transmemo.core.model.DarkThemeConfig
import com.chrysalide.transmemo.core.model.UserData
import kotlinx.coroutines.flow.Flow

class TransMemoUserDataRepository(
    private val preferencesDataSource: PreferencesDataSource
) : UserDataRepository {
    override val userData: Flow<UserData> = preferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        preferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }
}
