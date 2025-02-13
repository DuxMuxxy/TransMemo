package com.chrysalide.transmemo.presentation.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute // route to Settings screen

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(route = SettingsRoute, navOptions)

/**
 *  The Settings screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.settingsScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<SettingsRoute> {
        SettingsScreen(onShowSnackbar = onShowSnackbar)
    }
}
