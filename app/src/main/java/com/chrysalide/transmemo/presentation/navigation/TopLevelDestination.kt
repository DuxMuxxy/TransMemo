package com.chrysalide.transmemo.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route
) {
    HOME(
        selectedIcon = TransMemoIcons.Home,
        unselectedIcon = TransMemoIcons.HomeBorder,
        iconTextId = string.feature_home_title,
        titleTextId = string.app_name,
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class
    )
}
