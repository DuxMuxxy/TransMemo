package com.chrysalide.transmemo.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.NavigationDrawerScaffold
import com.chrysalide.transmemo.presentation.design.TransMemoBackground
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.design.TransMemoTopAppBar
import com.chrysalide.transmemo.presentation.navigation.TransMemoNavHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TransMemoApp(appState: TransMemoAppState) {
    TransMemoBackground {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        val currentDestination = appState.currentDestination
        val destination = appState.currentTopLevelDestination

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        ) {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            NavigationDrawerScaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                navigationItems = {
                    appState.topLevelDestinations.forEach { destination ->
                        val isSelected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                        NavigationDrawerItem(
                            label = { Text(stringResource(destination.iconTextId)) },
                            selected = isSelected,
                            onClick = {
                                appState.navigateToTopLevelDestination(destination)
                                coroutineScope.launch { drawerState.close() }
                            },
                            icon = {
                                if (isSelected) {
                                    Icon(
                                        imageVector = destination.selectedIcon,
                                        contentDescription = null
                                    )
                                } else {
                                    Icon(
                                        imageVector = destination.unselectedIcon,
                                        contentDescription = null
                                    )
                                }
                            },
                        )
                    }
                },
                drawerState = drawerState,
                snackbarHostState = snackbarHostState,
                navigateToChrysalide = {},
                navigateToHelpUs = {},
                navigateToFacebook = {},
                navigateToContributors = {},
                navigateToHelp = {},
                navigateToAbout = {}
            ) { padding ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                ) {
                    destination?.let {
                        TransMemoTopAppBar(
                            titleRes = destination.titleTextId,
                            navigationIcon = TransMemoIcons.Menu,
                            navigationIconContentDescription = stringResource(string.menu_content_description),
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                            onNavigationClick = {
                                coroutineScope.launch { drawerState.open() }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    }

                    Box(
                        modifier = Modifier.consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                    ) {
                        TransMemoNavHost(
                            appState = appState,
                            onShowSnackbar = { message, action ->
                                snackbarHostState.showSnackbar(message = message, actionLabel = action, duration = Short) == ActionPerformed
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false
