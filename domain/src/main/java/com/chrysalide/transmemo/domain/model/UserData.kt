package com.chrysalide.transmemo.domain.model

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val legacyDatabaseHasBeenImported: Boolean = false,
    val askAuthentication: Boolean = false,
    val useAlternativeAppIconAndName: Boolean = false
)
