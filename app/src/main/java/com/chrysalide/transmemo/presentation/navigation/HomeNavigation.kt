package com.chrysalide.transmemo.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute // route to Home screen

@Serializable
data object HomeBaseRoute // route to base navigation graph

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)

/**
 *  The Home screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.homeScreen() {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            // HomeScreen()
        }
    }
}
