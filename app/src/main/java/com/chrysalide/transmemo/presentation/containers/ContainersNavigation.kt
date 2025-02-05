package com.chrysalide.transmemo.presentation.containers

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.chrysalide.transmemo.presentation.containers.ContainersScreen
import kotlinx.serialization.Serializable

@Serializable
data object ContainersRoute // route to Containers screen

@Serializable
data object ContainersBaseRoute // route to base navigation graph

fun NavController.navigateToContainers(navOptions: NavOptions) = navigate(route = ContainersRoute, navOptions)

/**
 *  The Containers screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.containersScreen(onShowSnackbar: suspend (String, String?) -> Boolean) {
    navigation<ContainersBaseRoute>(startDestination = ContainersRoute) {
        composable<ContainersRoute> {
            ContainersScreen(onShowSnackbar = onShowSnackbar)
        }
    }
}
