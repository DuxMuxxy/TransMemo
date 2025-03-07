package com.chrysalide.transmemo.presentation.history

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HistoryRoute // route to History screen

fun NavController.navigateToHistory(navOptions: NavOptions) = navigate(route = HistoryRoute, navOptions)

/**
 *  The History screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.historyScreen() {
    composable<HistoryRoute> {
        HistoryScreen()
    }
}
