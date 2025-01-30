package com.chrysalide.transmemo.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.core.model.DarkThemeConfig
import com.chrysalide.transmemo.core.model.DarkThemeConfig.DARK
import com.chrysalide.transmemo.core.model.DarkThemeConfig.FOLLOW_SYSTEM
import com.chrysalide.transmemo.core.model.DarkThemeConfig.LIGHT
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.Loading
import com.chrysalide.transmemo.presentation.settings.SettingsUiState.UserEditableSettings
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    SettingsView(
        settingsUiState = settingsUiState,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
    )
}

@Composable
private fun SettingsView(
    settingsUiState: SettingsUiState,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
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
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator()
                }
            }

            is UserEditableSettings -> {
                SettingsPanel(
                    settings = settingsUiState,
                    onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserEditableSettings,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    AppearancePanel(settings, onChangeDarkThemeConfig)
}

@Composable
private fun ColumnScope.AppearancePanel(
    settings: UserEditableSettings,
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
            selected = settings.darkThemeConfig == FOLLOW_SYSTEM,
            label = { Text(stringResource(string.feature_settings_dark_mode_config_system_default)) }
        )
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
            onClick = { onChangeDarkThemeConfig(LIGHT) },
            selected = settings.darkThemeConfig == LIGHT,
            label = { Text(stringResource(string.feature_settings_dark_mode_config_light)) }
        )
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
            onClick = { onChangeDarkThemeConfig(DARK) },
            selected = settings.darkThemeConfig == DARK,
            label = { Text(stringResource(string.feature_settings_dark_mode_config_dark)) }
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

@ThemePreviews
@Composable
private fun PreviewSettingsDialog() {
    TransMemoTheme {
        SettingsView(
            settingsUiState = UserEditableSettings(darkThemeConfig = FOLLOW_SYSTEM),
            onChangeDarkThemeConfig = {},
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewSettingsDialogLoading() {
    TransMemoTheme {
        SettingsView(
            settingsUiState = Loading,
            onChangeDarkThemeConfig = {},
        )
    }
}
