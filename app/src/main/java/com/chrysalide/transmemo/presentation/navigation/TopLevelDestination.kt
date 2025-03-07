package com.chrysalide.transmemo.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.about.AboutBaseRoute
import com.chrysalide.transmemo.presentation.about.AboutMenuRoute
import com.chrysalide.transmemo.presentation.calendar.CalendarBaseRoute
import com.chrysalide.transmemo.presentation.calendar.CalendarRoute
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.history.HistoryRoute
import com.chrysalide.transmemo.presentation.inventory.InventoryRoute
import com.chrysalide.transmemo.presentation.products.ProductsBaseRoute
import com.chrysalide.transmemo.presentation.products.ProductsRoute
import com.chrysalide.transmemo.presentation.settings.SettingsRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route
) {
    CALENDAR(
        selectedIcon = TransMemoIcons.Calendar,
        unselectedIcon = TransMemoIcons.CalendarUnselected,
        iconTextId = string.feature_calendar_title,
        titleTextId = string.feature_calendar_title,
        route = CalendarRoute::class,
        baseRoute = CalendarBaseRoute::class
    ),
    INTAKES(
        selectedIcon = TransMemoIcons.History,
        unselectedIcon = TransMemoIcons.HistoryUnselected,
        iconTextId = string.feature_history_title,
        titleTextId = string.feature_history_title,
        route = HistoryRoute::class,
        baseRoute = HistoryRoute::class
    ),
    INVENTORY(
        selectedIcon = TransMemoIcons.Inventory,
        unselectedIcon = TransMemoIcons.InventoryUnselected,
        iconTextId = string.feature_inventory_title,
        titleTextId = string.feature_inventory_title,
        route = InventoryRoute::class,
        baseRoute = InventoryRoute::class
    ),
    PRODUCTS(
        selectedIcon = TransMemoIcons.Products,
        unselectedIcon = TransMemoIcons.ProductsUnselected,
        iconTextId = string.feature_products_title,
        titleTextId = string.feature_products_title,
        route = ProductsRoute::class,
        baseRoute = ProductsBaseRoute::class
    ),
    WELLBEING(
        selectedIcon = TransMemoIcons.Wellbeing,
        unselectedIcon = TransMemoIcons.WellbeingUnselected,
        iconTextId = string.feature_wellbeing_title,
        titleTextId = string.feature_wellbeing_title,
        route = CalendarRoute::class,
        baseRoute = CalendarBaseRoute::class
    ),
    STATISTICS(
        selectedIcon = TransMemoIcons.Statistics,
        unselectedIcon = TransMemoIcons.StatisticsUnselected,
        iconTextId = string.feature_statistics_title,
        titleTextId = string.feature_statistics_title,
        route = CalendarRoute::class,
        baseRoute = CalendarBaseRoute::class
    ),
    SETTINGS(
        selectedIcon = TransMemoIcons.Settings,
        unselectedIcon = TransMemoIcons.SettingsUnselected,
        iconTextId = string.feature_settings_title,
        titleTextId = string.feature_settings_title,
        route = SettingsRoute::class,
        baseRoute = SettingsRoute::class
    ),
    ABOUT(
        selectedIcon = TransMemoIcons.About,
        unselectedIcon = TransMemoIcons.AboutUnselected,
        iconTextId = string.feature_about_title,
        titleTextId = string.feature_about_title,
        route = AboutMenuRoute::class,
        baseRoute = AboutBaseRoute::class
    )
}
