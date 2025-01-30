package com.chrysalide.transmemo.presentation.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute // route to Settings screen

@Serializable
data object SettingsBaseRoute // route to base navigation graph

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(route = SettingsRoute, navOptions)

/**
 *  The Settings screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.settingsScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    navigation<SettingsBaseRoute>(startDestination = SettingsRoute) {
        composable<SettingsRoute> {
            SettingsScreen(onShowSnackbar = onShowSnackbar)
        }
    }
}
