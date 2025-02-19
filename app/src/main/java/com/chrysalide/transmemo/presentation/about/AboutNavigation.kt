package com.chrysalide.transmemo.presentation.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object AboutMenuRoute // route to About menu screen

@Serializable
data object AboutChrysalideRoute // route to About Chrysalide screen

@Serializable
data object AboutContributorsRoute // route to Contributors screen

@Serializable
data object AboutHelpRoute // route to Help screen

@Serializable
data object AboutLicensesRoute // route to Licenses screen

@Serializable
data object AboutBaseRoute // route to base navigation graph

fun NavController.navigateToAbout(navOptions: NavOptions) = navigate(route = AboutMenuRoute, navOptions)

fun NavController.navigateToAboutChrysalide() = navigate(route = AboutChrysalideRoute)

fun NavController.navigateToAboutContributors() = navigate(route = AboutContributorsRoute)

fun NavController.navigateToAboutHelp() = navigate(route = AboutHelpRoute)

fun NavController.navigateToAboutLicenses() = navigate(route = AboutLicensesRoute)

/**
 *  The About graph of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.aboutGraph(
    navigateToHelp: () -> Unit,
    navigateToChrysalide: () -> Unit,
    navigateToContributors: () -> Unit,
    navigateToLicenses: () -> Unit,
    navigateUp: () -> Unit
) {
    navigation<AboutBaseRoute>(startDestination = AboutMenuRoute) {
        composable<AboutMenuRoute> {
            AboutMenuScreen(
                navigateToHelp,
                navigateToChrysalide,
                navigateToContributors,
                navigateToLicenses
            )
        }
        composable<AboutHelpRoute> {
            AboutHelpScreen(navigateUp)
        }
        composable<AboutChrysalideRoute> {
            AboutChrysalideScreen(navigateUp)
        }
        composable<AboutContributorsRoute> {
            AboutContributorsScreen(navigateUp)
        }
        composable<AboutLicensesRoute> {
            AboutLicensesScreen(navigateUp)
        }
    }
}
