package com.chrysalide.transmemo.presentation.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object AboutMenuRoute // route to About screen

@Serializable
data object AboutBaseRoute // route to base navigation graph

fun NavController.navigateToAbout(navOptions: NavOptions) = navigate(route = AboutMenuRoute, navOptions)

/**
 *  The About screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.aboutScreen() {
    navigation<AboutBaseRoute>(startDestination = AboutMenuRoute) {
        composable<AboutMenuRoute> {
            AboutMenuScreen(
                navigateToChrysalide = {},
                navigateToHelpUs = {},
                navigateToFacebook = {},
                navigateToContributors = {},
                navigateToHelp = {}
            )
        }
    }
}
