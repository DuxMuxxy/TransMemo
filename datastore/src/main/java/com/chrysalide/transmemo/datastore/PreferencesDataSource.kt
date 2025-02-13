package com.chrysalide.transmemo.datastore

import androidx.datastore.core.DataStore
import com.chrysalide.transmemo.core.datastore.DarkThemeConfigProto
import com.chrysalide.transmemo.core.datastore.UserPreferences
import com.chrysalide.transmemo.core.datastore.copy
import com.chrysalide.transmemo.domain.model.DarkThemeConfig
import com.chrysalide.transmemo.domain.model.UserData
import kotlinx.coroutines.flow.map

internal class PreferencesDataSource(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userData = userPreferences.data.map {
        UserData(
            darkThemeConfig = when (it.darkThemeConfig) {
                DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                else -> DarkThemeConfig.FOLLOW_SYSTEM
            },
            useDynamicColor = it.useDynamicColor,
            legacyDatabaseHasBeenImported = it.legacyDatabaseHasBeenImported,
            askAuthentication = it.askAuthentication,
            useAlternativeAppIconAndName = it.useAlternativeAppIconAndName
        )
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    suspend fun setLegacyDatabaseHasBeenImported() {
        userPreferences.updateData {
            it.copy { this.legacyDatabaseHasBeenImported = true }
        }
    }

    suspend fun setAskAuthentication(askAuthentication: Boolean) {
        userPreferences.updateData {
            it.copy { this.askAuthentication = askAuthentication }
        }
    }

    suspend fun setUseAlternativeAppIconAndName(useAlternativeAppIconAndName: Boolean) {
        userPreferences.updateData {
            it.copy { this.useAlternativeAppIconAndName = useAlternativeAppIconAndName }
        }
    }
}
