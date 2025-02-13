package com.chrysalide.transmemo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.chrysalide.transmemo.presentation.TransMemoAppState
import com.chrysalide.transmemo.presentation.about.aboutGraph
import com.chrysalide.transmemo.presentation.about.navigateToAboutChrysalide
import com.chrysalide.transmemo.presentation.about.navigateToAboutContributors
import com.chrysalide.transmemo.presentation.about.navigateToAboutHelp
import com.chrysalide.transmemo.presentation.calendar.CalendarBaseRoute
import com.chrysalide.transmemo.presentation.calendar.calendarGraph
import com.chrysalide.transmemo.presentation.intakes.intakesScreen
import com.chrysalide.transmemo.presentation.inventory.inventoryScreen
import com.chrysalide.transmemo.presentation.products.productsScreen
import com.chrysalide.transmemo.presentation.settings.settingsScreen

@Composable
fun TransMemoNavHost(
    appState: TransMemoAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = CalendarBaseRoute,
        modifier = modifier
    ) {
        calendarGraph()
        intakesScreen()
        inventoryScreen(onShowSnackbar)
        productsScreen(onShowSnackbar)
        settingsScreen(onShowSnackbar)
        aboutGraph(
            navigateToChrysalide = { navController.navigateToAboutChrysalide() },
            navigateToContributors = { navController.navigateToAboutContributors() },
            navigateToHelp = { navController.navigateToAboutHelp() },
            navigateUp = { navController.navigateUp() }
        )
    }
}
