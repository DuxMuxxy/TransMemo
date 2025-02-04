package com.chrysalide.transmemo.presentation.products

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object ProductsRoute // route to Products screen

@Serializable
data object IncomingEventRoute // route to Products screen

@Serializable
data object ProductsBaseRoute // route to base navigation graph

fun NavController.navigateToProducts(navOptions: NavOptions) = navigate(route = ProductsRoute, navOptions)

/**
 *  The Products screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.productsScreen(onShowSnackbar: suspend (String, String?) -> Boolean) {
    navigation<ProductsBaseRoute>(startDestination = ProductsRoute) {
        composable<ProductsRoute> {
            ProductsScreen(onShowSnackbar = onShowSnackbar)
        }
    }
}
