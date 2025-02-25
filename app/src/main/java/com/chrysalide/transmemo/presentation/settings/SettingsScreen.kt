package com.chrysalide.transmemo.presentation.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.platform.LocalContext
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
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.Loading
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.UserEditableSettings
import com.chrysalide.transmemo.presentation.settings.notification.CustomNotificationMessageDialog
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import com.chrysalide.transmemo.presentation.theme.supportsDynamicTheming
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
        uiState = uiState,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
        onChangeDynamicColorPreference = viewModel::updateDynamicColorPreference,
        setAskAuthentication = viewModel::updateAskAuthentication,
        setUseAlternativeAppIconAndName = viewModel::updateUseAlternativeAppIconAndName,
        setUseCustomNotificationMessage = viewModel::updateUseCustomNotificationMessage,
        setCustomNotificationMessage = viewModel::updateCustomNotificationMessage,
        importDatabaseFile = viewModel::importDatabaseFile
    )
}

@Composable
private fun SettingsView(
    uiState: SettingsUiState,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeDynamicColorPreference: (Boolean) -> Unit,
    setAskAuthentication: (Boolean) -> Unit,
    setUseAlternativeAppIconAndName: (Boolean) -> Unit,
    setUseCustomNotificationMessage: (Boolean) -> Unit,
    setCustomNotificationMessage: (String) -> Unit,
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

    var showCustomNotificationMessageDialog by remember { mutableStateOf(false) }
    if (showCustomNotificationMessageDialog && uiState is UserEditableSettings) {
        CustomNotificationMessageDialog(
            customMessage = uiState.customNotificationMessage,
            onDismiss = { showCustomNotificationMessageDialog = false },
            onConfirm = {
                setCustomNotificationMessage(it)
                showCustomNotificationMessageDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        when (uiState) {
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
                    settings = uiState,
                    onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                    onChangeDynamicColorPreference = onChangeDynamicColorPreference,
                    onChangeAskAuthentication = { askAuthentication ->
                        if (askAuthentication) {
                            showBiometricPrompt = true
                        } else {
                            setAskAuthentication(false)
                        }
                    },
                    onChangeUseAlternativeAppIconAndName = setUseAlternativeAppIconAndName,
                    onChangeUseCustomNotificationMessage = setUseCustomNotificationMessage,
                    editCustomNotificationMessage = { showCustomNotificationMessageDialog = true },
                    onImportClick = { importFileLauncher.launch(arrayOf("*/*")) }
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserEditableSettings,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeDynamicColorPreference: (Boolean) -> Unit,
    onChangeAskAuthentication: (Boolean) -> Unit,
    onChangeUseAlternativeAppIconAndName: (Boolean) -> Unit,
    onChangeUseCustomNotificationMessage: (Boolean) -> Unit,
    editCustomNotificationMessage: () -> Unit,
    onImportClick: () -> Unit
) {
    AppearancePanel(
        settings.darkThemeConfig,
        settings.useDynamicColor,
        onChangeDarkThemeConfig,
        onChangeDynamicColorPreference
    )
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    SecurityPanel(
        settings.canDeviceAskAuthentication,
        settings.askAuthentication,
        settings.useAlternativeAppIconAndName,
        settings.useCustomNotificationMessage,
        onChangeAskAuthentication,
        onChangeUseAlternativeAppIconAndName,
        onChangeUseCustomNotificationMessage,
        editCustomNotificationMessage
    )
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    DatabasePanel(onImportClick)
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    NotificationsPanel()
}

@Composable
private fun ColumnScope.AppearancePanel(
    darkThemeConfig: DarkThemeConfig,
    useDynamicColors: Boolean,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeDynamicColorPreference: (Boolean) -> Unit
) {
    val supportDynamicColor: Boolean = supportsDynamicTheming()
    SettingsSectionTitle(text = stringResource(string.feature_settings_appearance_title))
    Spacer(modifier = Modifier.height(16.dp))
    SettingsSectionSubtitle(text = stringResource(string.feature_settings_dark_mode_preference))
    Spacer(modifier = Modifier.height(8.dp))
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

    if (supportDynamicColor) {
        Spacer(modifier = Modifier.height(32.dp))
        SettingsSectionSubtitle(text = stringResource(string.feature_settings_dynamic_colors_preference))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SettingsDescription(text = stringResource(string.feature_settings_dynamic_colors_title))
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            Switch(checked = useDynamicColors, onCheckedChange = onChangeDynamicColorPreference)
        }
    }
}

@Composable
private fun ColumnScope.SecurityPanel(
    canDeviceAskAuthentication: Boolean,
    askAuthentication: Boolean,
    useAlternativeAppIconAndName: Boolean,
    useCustomNotificationMessage: Boolean,
    onChangeAskAuthentication: (Boolean) -> Unit,
    onChangeUseAlternativeAppIconAndName: (Boolean) -> Unit,
    onChangeUseCustomNotificationMessage: (Boolean) -> Unit,
    editCustomNotificationMessage: () -> Unit
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
    Spacer(modifier = Modifier.height(32.dp))
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

        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .weight(1f)
                    .clickable { editCustomNotificationMessage() }
            ) {
                SettingsSectionSubtitle(text = stringResource(string.feature_settings_custom_notification_title))
                Spacer(modifier = Modifier.height(4.dp))
                SettingsDescription(text = stringResource(string.feature_settings_custom_notification_description))
            }
            Spacer(Modifier.width(16.dp))
            Icon(
                TransMemoIcons.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.width(16.dp))
            VerticalDivider(modifier = Modifier.height(34.dp))
            Spacer(Modifier.width(24.dp))
            Switch(
                checked = useCustomNotificationMessage,
                onCheckedChange = onChangeUseCustomNotificationMessage
            )
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
private fun ColumnScope.NotificationsPanel() {
    val context = LocalContext.current
    SettingsSectionTitle(text = stringResource(string.feature_settings_notifications_title))
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { openNotificationSettings(context) }.padding(vertical = 16.dp)
    ) {
        Icon(
            TransMemoIcons.Notifications,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.width(24.dp))
        SettingsSectionSubtitle(text = stringResource(string.feature_settings_notifications_title))
        Spacer(Modifier.weight(1f))
        Icon(
            TransMemoIcons.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
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

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

@ThemePreviews
@Composable
private fun SettingsScreenLoadingPreview() {
    TransMemoTheme {
        SettingsView(
            uiState = Loading,
            onChangeDarkThemeConfig = {},
            onChangeDynamicColorPreference = {},
            setAskAuthentication = {},
            setUseAlternativeAppIconAndName = {},
            setUseCustomNotificationMessage = {},
            setCustomNotificationMessage = {},
            importDatabaseFile = {}
        )
    }
}

@ThemePreviews
@Composable
private fun SettingsScreenPreview() {
    TransMemoTheme {
        SettingsView(
            uiState = UserEditableSettings(
                darkThemeConfig = FOLLOW_SYSTEM,
                useDynamicColor = false,
                canDeviceAskAuthentication = true,
                askAuthentication = true,
                useAlternativeAppIconAndName = false,
                useCustomNotificationMessage = false,
                customNotificationMessage = ""
            ),
            onChangeDarkThemeConfig = {},
            onChangeDynamicColorPreference = {},
            setAskAuthentication = {},
            setUseAlternativeAppIconAndName = {},
            setUseCustomNotificationMessage = {},
            setCustomNotificationMessage = {},
            importDatabaseFile = {}
        )
    }
}

@ThemePreviews
@Composable
private fun SettingsScreenNoDeviceAuthenticatorPreview() {
    TransMemoTheme {
        SettingsView(
            uiState = UserEditableSettings(
                darkThemeConfig = FOLLOW_SYSTEM,
                useDynamicColor = false,
                canDeviceAskAuthentication = false,
                askAuthentication = false,
                useAlternativeAppIconAndName = false,
                useCustomNotificationMessage = false,
                customNotificationMessage = ""
            ),
            onChangeDarkThemeConfig = {},
            onChangeDynamicColorPreference = {},
            setAskAuthentication = {},
            setUseAlternativeAppIconAndName = {},
            setUseCustomNotificationMessage = {},
            setCustomNotificationMessage = {},
            importDatabaseFile = {}
        )
    }
}
