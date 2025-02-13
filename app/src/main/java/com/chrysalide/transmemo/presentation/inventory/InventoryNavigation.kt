package com.chrysalide.transmemo.presentation.inventory

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object InventoryRoute // route to Containers screen

fun NavController.navigateToInventory(navOptions: NavOptions) = navigate(route = InventoryRoute, navOptions)

/**
 *  The Containers screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.inventoryScreen(onShowSnackbar: suspend (String, String?) -> Boolean) {
    composable<InventoryRoute> {
        InventoryScreen(onShowSnackbar = onShowSnackbar)
    }
}
