package com.chrysalide.transmemo.core.model

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val legacyDatabaseHasBeenImported: Boolean = false
)
