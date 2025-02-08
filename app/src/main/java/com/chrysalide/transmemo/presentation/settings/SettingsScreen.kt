package com.chrysalide.transmemo.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.model.DarkThemeConfig
import com.chrysalide.transmemo.domain.model.DarkThemeConfig.DARK
import com.chrysalide.transmemo.domain.model.DarkThemeConfig.FOLLOW_SYSTEM
import com.chrysalide.transmemo.domain.model.DarkThemeConfig.LIGHT
import com.chrysalide.transmemo.presentation.design.BiometricPromptContainer
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.Loading
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.UserEditableSettings
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    val importResultSnackbar = viewModel.importResultSnackbar
    val importResultSuccessText = stringResource(string.feature_settings_database_import_success)
    val importResultErrorText = stringResource(string.feature_settings_database_import_error)

    LaunchedEffect(importResultSnackbar) {
        when (importResultSnackbar) {
            ImportDatabaseFileResultSnackbar.Idle -> {}
            ImportDatabaseFileResultSnackbar.Success -> {
                onShowSnackbar(importResultSuccessText, null)
                viewModel.importResultSnackbar = ImportDatabaseFileResultSnackbar.Idle
            }
            ImportDatabaseFileResultSnackbar.Error -> {
                onShowSnackbar(importResultErrorText, null)
                viewModel.importResultSnackbar = ImportDatabaseFileResultSnackbar.Idle
            }
        }
    }

    SettingsView(
        settingsUiState = settingsUiState,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
        setAskAuthentication = viewModel::updateAskAuthentication,
        setUseAlternativeAppIconAndName = viewModel::updateUseAlternativeAppIconAndName,
        importDatabaseFile = viewModel::importDatabaseFile
    )
}

@Composable
private fun SettingsView(
    settingsUiState: SettingsUiState,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    setAskAuthentication: (Boolean) -> Unit,
    setUseAlternativeAppIconAndName: (Boolean) -> Unit,
    importDatabaseFile: (Uri) -> Unit
) {
    val importFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { fileUri ->
        fileUri?.let(importDatabaseFile)
    }

    var showBiometricPrompt by remember { mutableStateOf(false) }
    if (showBiometricPrompt) {
        BiometricPromptContainer(
            onAuthSucceeded = { setAskAuthentication(true) }
        )
        showBiometricPrompt = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        when (settingsUiState) {
            Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            is UserEditableSettings -> {
                SettingsPanel(
                    settings = settingsUiState,
                    onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                    onChangeAskAuthentication = { askAuthentication ->
                        if (askAuthentication) {
                            showBiometricPrompt = true
                        } else {
                            setAskAuthentication(false)
                        }
                    },
                    onChangeUseAlternativeAppIconAndName = setUseAlternativeAppIconAndName,
                    onImportClick = { importFileLauncher.launch(arrayOf("*/*")) },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserEditableSettings,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeAskAuthentication: (Boolean) -> Unit,
    onChangeUseAlternativeAppIconAndName: (Boolean) -> Unit,
    onImportClick: () -> Unit
) {
    AppearancePanel(settings.darkThemeConfig, onChangeDarkThemeConfig)
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    SecurityPanel(
        settings.canDeviceAskAuthentication,
        settings.askAuthentication,
        settings.useAlternativeAppIconAndName,
        onChangeAskAuthentication,
        onChangeUseAlternativeAppIconAndName,
    )
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    DatabasePanel(onImportClick)
}

@Composable
private fun ColumnScope.AppearancePanel(
    darkThemeConfig: DarkThemeConfig,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit
) {
    SettingsSectionTitle(text = stringResource(string.feature_settings_appearance_title))
    Spacer(modifier = Modifier.height(16.dp))
    SettingsSectionSubtitle(text = stringResource(string.feature_settings_dark_mode_preference))
    Spacer(modifier = Modifier.height(16.dp))
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
            onClick = { onChangeDarkThemeConfig(FOLLOW_SYSTEM) },
            selected = darkThemeConfig == FOLLOW_SYSTEM,
            label = { Text(stringResource(string.feature_settings_dark_mode_config_system_default)) }
        )
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
            onClick = { onChangeDarkThemeConfig(LIGHT) },
            selected = darkThemeConfig == LIGHT,
            label = { Text(stringResource(string.feature_settings_dark_mode_config_light)) }
        )
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
            onClick = { onChangeDarkThemeConfig(DARK) },
            selected = darkThemeConfig == DARK,
            label = { Text(stringResource(string.feature_settings_dark_mode_config_dark)) }
        )
    }
}

@Composable
private fun ColumnScope.SecurityPanel(
    canDeviceAskAuthentication: Boolean,
    askAuthentication: Boolean,
    useAlternativeAppIconAndName: Boolean,
    onChangeAskAuthentication: (Boolean) -> Unit,
    onChangeUseAlternativeAppIconAndName: (Boolean) -> Unit
) {
    var showAlternativeIconPreview by remember { mutableStateOf(false) }

    SettingsSectionTitle(text = stringResource(string.feature_settings_security_title))
    if (canDeviceAskAuthentication) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
            ) {
                SettingsSectionSubtitle(text = stringResource(string.feature_settings_ask_authentication_title))
                Spacer(modifier = Modifier.height(4.dp))
                SettingsDescription(text = stringResource(string.feature_settings_ask_authentication_description))
            }
            Switch(checked = askAuthentication, onCheckedChange = onChangeAskAuthentication)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                SettingsSectionSubtitle(text = stringResource(string.feature_settings_alt_app_icon_title))
                Spacer(modifier = Modifier.height(4.dp))
                SettingsDescription(text = stringResource(string.feature_settings_alt_app_icon_description))
            }
            Switch(
                checked = useAlternativeAppIconAndName,
                onCheckedChange = {
                    onChangeUseAlternativeAppIconAndName(it)
                    showAlternativeIconPreview = it
                }
            )
        }
        AnimatedVisibility(showAlternativeIconPreview) {
            AlternativeIconPreview()
        }
    }
}

@Composable
private fun AlternativeIconPreview() {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp),
            ) {
                Text(stringResource(string.feature_settings_alt_app_icon_example))
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    ImageBitmap.imageResource(R.mipmap.ic_alternative_todo_foreground),
                    contentDescription = null,
                    modifier = Modifier.background(Color.White, shape = RoundedCornerShape(100)),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(string.app_name_alternative_todo), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ColumnScope.DatabasePanel(
    onImportClick: () -> Unit
) {
    SettingsSectionTitle(text = stringResource(string.feature_settings_database_title))
    Spacer(modifier = Modifier.height(16.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
            Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            SettingsSectionSubtitle(text = stringResource(string.feature_settings_database_import_title))
            Spacer(modifier = Modifier.height(4.dp))
            SettingsDescription(text = stringResource(string.feature_settings_database_import_description))
        }
        OutlinedButton(onClick = onImportClick) {
            Text(stringResource(string.feature_settings_database_import_action))
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun SettingsSectionSubtitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
private fun SettingsDescription(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@ThemePreviews
@Composable
private fun SettingsScreenLoadingPreview() {
    TransMemoTheme {
        SettingsView(
            settingsUiState = Loading,
            onChangeDarkThemeConfig = {},
            setAskAuthentication = {},
            setUseAlternativeAppIconAndName = {},
            importDatabaseFile = {}
        )
    }
}

@ThemePreviews
@Composable
private fun SettingsScreenPreview() {
    TransMemoTheme {
        SettingsView(
            settingsUiState = UserEditableSettings(
                darkThemeConfig = FOLLOW_SYSTEM,
                canDeviceAskAuthentication = true,
                askAuthentication = true,
                useAlternativeAppIconAndName = false
            ),
            onChangeDarkThemeConfig = {},
            setAskAuthentication = {},
            setUseAlternativeAppIconAndName = {},
            importDatabaseFile = {}
        )
    }
}

@ThemePreviews
@Composable
private fun SettingsScreenNoDeviceAuthenticatorPreview() {
    TransMemoTheme {
        SettingsView(
            settingsUiState = UserEditableSettings(
                darkThemeConfig = FOLLOW_SYSTEM,
                canDeviceAskAuthentication = false,
                askAuthentication = false,
                useAlternativeAppIconAndName = false
            ),
            onChangeDarkThemeConfig = {},
            setAskAuthentication = {},
            setUseAlternativeAppIconAndName = {},
            importDatabaseFile = {}
        )
    }
}
