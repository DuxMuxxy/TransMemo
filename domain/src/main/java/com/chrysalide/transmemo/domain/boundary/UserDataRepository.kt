package com.chrysalide.transmemo.domain.boundary

import com.chrysalide.transmemo.domain.model.DarkThemeConfig
import com.chrysalide.transmemo.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    suspend fun setLegacyDatabaseHasBeenImported()

    suspend fun setAskAuthentication(askAuthentication: Boolean)
}
