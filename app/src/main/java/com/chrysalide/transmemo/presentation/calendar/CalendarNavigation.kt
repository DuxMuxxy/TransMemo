package com.chrysalide.transmemo.presentation.calendar

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object CalendarRoute // route to Calendar screen

@Serializable
data object IncomingEventRoute // route to Calendar screen

@Serializable
data object CalendarBaseRoute // route to base navigation graph

fun NavController.navigateToCalendar(navOptions: NavOptions) = navigate(route = CalendarRoute, navOptions)

/**
 *  The Calendar screen of the app.
 *  This should be supplied from a separate module.
 */
fun NavGraphBuilder.calendarGraph() {
    navigation<CalendarBaseRoute>(startDestination = CalendarRoute) {
        composable<CalendarRoute> {
            CalendarScreen(
                navigateToIncomingEvent = {}
            )
        }
        composable<IncomingEventRoute> {
            // IncomingEventScreen()
        }
    }
}
