package com.chrysalide.transmemo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.chrysalide.transmemo.presentation.TransMemoAppState
import com.chrysalide.transmemo.presentation.calendar.CalendarBaseRoute
import com.chrysalide.transmemo.presentation.calendar.calendarScreen
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
        calendarScreen()
        productsScreen()
        settingsScreen(onShowSnackbar)
    }
}
