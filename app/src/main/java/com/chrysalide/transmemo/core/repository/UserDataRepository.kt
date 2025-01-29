package com.chrysalide.transmemo.core.repository

import com.chrysalide.transmemo.core.model.DarkThemeConfig
import com.chrysalide.transmemo.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Sets the desired dark theme config
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
}
