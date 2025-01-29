package com.chrysalide.transmemo.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.TransMemoBackground
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.design.TransMemoTopAppBar
import com.chrysalide.transmemo.presentation.navigation.TransMemoNavHost
import com.chrysalide.transmemo.presentation.settings.SettingsDialog
import kotlin.reflect.KClass

@Composable
fun TransMemoApp(
    appState: TransMemoAppState,
    modifier: Modifier = Modifier,
) {
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    TransMemoBackground(modifier = modifier) {
        val snackbarHostState = remember { SnackbarHostState() }

        TransMemoApp(
            appState = appState,
            snackbarHostState = snackbarHostState,
            showSettingsDialog = showSettingsDialog,
            onSettingsDismissed = { showSettingsDialog = false },
            onTopAppBarActionClick = { showSettingsDialog = true },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TransMemoApp(
    appState: TransMemoAppState,
    snackbarHostState: SnackbarHostState,
    showSettingsDialog: Boolean,
    onSettingsDismissed: () -> Unit,
    onTopAppBarActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination

    if (showSettingsDialog) {
        SettingsDialog(onDismiss = onSettingsDismissed)
    }

    // val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        // Show the top app bar on top level destinations.
        val destination = appState.currentTopLevelDestination
        var shouldShowTopAppBar = false

        if (destination != null) {
            shouldShowTopAppBar = true
            TransMemoTopAppBar(
                titleRes = destination.titleTextId,
                actionIcon = TransMemoIcons.Settings,
                actionIconContentDescription = stringResource(string.feature_settings_top_app_bar_action_icon_description),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                onActionClick = { onTopAppBarActionClick() },
            )
        }

        Box(
            modifier = Modifier.consumeWindowInsets(
                if (shouldShowTopAppBar) {
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                } else {
                    WindowInsets(0, 0, 0, 0)
                }
            )
        ) {
            TransMemoNavHost(
                appState = appState,
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(message = message, actionLabel = action, duration = Short) == ActionPerformed
                },
            )
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false
