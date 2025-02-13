package com.chrysalide.transmemo.presentation.design

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun TMSubScreen(
    @StringRes titleRes: Int,
    icon: ImageVector,
    navigateUp: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).systemBarsPadding()
    ) {
        IconButton(navigateUp, modifier = Modifier.padding(8.dp)) {
            Icon(TransMemoIcons.Back, stringResource(titleRes))
        }
        Spacer(Modifier.height(16.dp))
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            stringResource(titleRes),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = 24.dp).align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@ThemePreviews
@Composable
private fun TMSubScreenPreview() {
    TransMemoTheme {
        TMSubScreen(string.feature_about_contributors_title, TransMemoIcons.Contributors, {}, {})
    }
}
