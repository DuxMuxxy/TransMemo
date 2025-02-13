package com.chrysalide.transmemo.presentation.about

import androidx.compose.runtime.Composable
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.TMSubScreen
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AboutContributorsScreen(navigateUp: () -> Unit) {
    TMSubScreen(
        titleRes = string.feature_about_contributors_title,
        icon = TransMemoIcons.Contributors,
        navigateUp = navigateUp
    ) {
    }
}

@ThemePreviews
@Composable
private fun AboutContributorsScreenPreview() {
    TransMemoTheme {
        AboutContributorsScreen({})
    }
}
