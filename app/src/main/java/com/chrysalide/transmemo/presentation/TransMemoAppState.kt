package com.chrysalide.transmemo.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.chrysalide.transmemo.presentation.calendar.navigateToCalendar
import com.chrysalide.transmemo.presentation.navigation.TopLevelDestination
import com.chrysalide.transmemo.presentation.navigation.TopLevelDestination.CALENDAR
import com.chrysalide.transmemo.presentation.navigation.TopLevelDestination.CONTAINERS
import com.chrysalide.transmemo.presentation.navigation.TopLevelDestination.PRODUCTS
import com.chrysalide.transmemo.presentation.navigation.TopLevelDestination.SETTINGS
import com.chrysalide.transmemo.presentation.navigation.TopLevelDestination.STATISTICS
import com.chrysalide.transmemo.presentation.navigation.TopLevelDestination.TAKES
import com.chrysalide.transmemo.presentation.navigation.TopLevelDestination.WELLNESS
import com.chrysalide.transmemo.presentation.products.navigateToProducts
import com.chrysalide.transmemo.presentation.settings.navigateToSettings

@Composable
fun rememberTransMemoAppState(navController: NavHostController = rememberNavController()): TransMemoAppState =
    remember(navController) { TransMemoAppState(navController = navController) }

@Stable
class TransMemoAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) == true
            }
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            when (topLevelDestination) {
                CALENDAR -> navController.navigateToCalendar(topLevelNavOptions)
                TAKES -> {} // navController.navigateToTakes(topLevelNavOptions)
                CONTAINERS -> {} // navController.navigateToContainers(topLevelNavOptions)
                PRODUCTS -> navController.navigateToProducts(topLevelNavOptions)
                WELLNESS -> {} // navController.navigateToWellness(topLevelNavOptions)
                STATISTICS -> {} // navController.navigateToStatistics(topLevelNavOptions)
                SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
            }
        }
    }
}
