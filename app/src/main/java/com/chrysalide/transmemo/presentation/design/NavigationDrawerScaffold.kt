package com.chrysalide.transmemo.presentation.design

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chrysalide.transmemo.R.string

@Composable
fun NavigationDrawerScaffold(
    modifier: Modifier = Modifier,
    navigationItems: @Composable () -> Unit,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    navigateToChrysalide: () -> Unit,
    navigateToHelpUs: () -> Unit,
    navigateToFacebook: () -> Unit,
    navigateToContributors: () -> Unit,
    navigateToHelp: () -> Unit,
    navigateToAbout: () -> Unit,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(string.app_name), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    navigationItems()
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Chrysalide", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    NavigationDrawerItem(
                        label = { Text("Chrysalide") },
                        selected = false,
                        onClick = navigateToChrysalide
                    )
                    NavigationDrawerItem(
                        label = { Text("Aidez-nous") },
                        selected = false,
                        onClick = navigateToHelpUs
                    )
                    NavigationDrawerItem(
                        label = { Text("Groupe Facebook") },
                        selected = false,
                        onClick = navigateToFacebook
                    )
                    NavigationDrawerItem(
                        label = { Text("Contributeurs") },
                        selected = false,
                        onClick = navigateToContributors
                    )
                    NavigationDrawerItem(
                        label = { Text("Aide") },
                        selected = false,
                        onClick = navigateToHelp
                    )
                    NavigationDrawerItem(
                        label = { Text("À propos") },
                        selected = false,
                        onClick = navigateToAbout
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = modifier,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}
