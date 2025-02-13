package com.chrysalide.transmemo.presentation.intakes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object IntakesRoute // route to Intakes screen

fun NavController.navigateToIntakes(navOptions: NavOptions) = navigate(route = IntakesRoute, navOptions)

/**
 *  The Intakes screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.intakesScreen() {
    composable<IntakesRoute> {
        IntakesScreen()
    }
}
