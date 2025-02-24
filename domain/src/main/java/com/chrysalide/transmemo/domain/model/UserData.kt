package com.chrysalide.transmemo.domain.model

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean = false,
    val legacyDatabaseHasBeenImported: Boolean = false,
    val askAuthentication: Boolean = false,
    val useAlternativeAppIconAndName: Boolean = false,
    val useCustomNotificationMessage: Boolean = false,
    val customNotificationMessage: String
)
