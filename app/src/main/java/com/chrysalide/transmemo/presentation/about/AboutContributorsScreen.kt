package com.chrysalide.transmemo.presentation.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // TODO LOGO Chrysalide

            Spacer(Modifier.height(32.dp))
            Text(
                stringResource(string.feature_about_contributors_development_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text("Sophie Berthier\nDux Muxxy", textAlign = TextAlign.Center)

            Spacer(Modifier.height(32.dp))

            Text(
                stringResource(string.feature_about_contributors_translation_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(string.feature_about_contributors_translation_english))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                Text("David Latour")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(string.feature_about_contributors_translation_portuguese))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                Text("Bernardo Souza")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(string.feature_about_contributors_translation_spanish))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                Text("Laura Sorcière")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(string.feature_about_contributors_translation_czech))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                Text("Andris Rainke")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(string.feature_about_contributors_translation_german_swedish))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                Text("Alan Van Brackel")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(string.feature_about_contributors_translation_russian))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                Text("Anne B")
            }

            Spacer(Modifier.height(32.dp))

            Text(
                stringResource(string.feature_about_contributors_tests_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Alan Van Brackel\nChaton Nageur\nClémentine\nChris\nDavid Latour\nDusty\nRadis\nSam",
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@ThemePreviews
@Composable
private fun AboutContributorsScreenPreview() {
    TransMemoTheme {
        AboutContributorsScreen({})
    }
}
