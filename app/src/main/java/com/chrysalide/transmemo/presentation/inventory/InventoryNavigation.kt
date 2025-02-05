package com.chrysalide.transmemo.presentation.inventory

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object InventoryRoute // route to Containers screen

@Serializable
data object InventoryBaseRoute // route to base navigation graph

fun NavController.navigateToInventory(navOptions: NavOptions) = navigate(route = InventoryRoute, navOptions)

/**
 *  The Containers screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.inventoryScreen(onShowSnackbar: suspend (String, String?) -> Boolean) {
    navigation<InventoryBaseRoute>(startDestination = InventoryRoute) {
        composable<InventoryRoute> {
            InventoryScreen(onShowSnackbar = onShowSnackbar)
        }
    }
}
