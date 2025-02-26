package com.chrysalide.transmemo.presentation.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.chrysalide.transmemo.BuildConfig
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.ChrysalideLogoFull
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AboutMenuScreen(
    navigateToHelp: () -> Unit,
    navigateToChrysalide: () -> Unit,
    navigateToContributors: () -> Unit,
    navigateToLicenses: () -> Unit
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MenuItem(
            stringResource(string.feature_about_help_title),
            icon = TransMemoIcons.Help,
            onClick = navigateToHelp,
        )
        HorizontalDivider()
        MenuItem(
            stringResource(string.feature_about_chrysalide_asso_title),
            icon = painterResource(R.drawable.logo_chrysalide),
            onClick = navigateToChrysalide
        )
        HorizontalDivider()
        MenuItem(
            stringResource(string.feature_about_contributors_title),
            icon = TransMemoIcons.Contributors,
            onClick = navigateToContributors,
        )
        HorizontalDivider()
        MenuItem(
            stringResource(string.feature_about_licenses_title),
            icon = TransMemoIcons.Licenses,
            onClick = navigateToLicenses,
        )
        HorizontalDivider()

        Spacer(Modifier.height(32.dp))

        val chrysalideLink = stringResource(string.feature_about_menu_link)
        val helpUsLink = stringResource(string.feature_about_help_us_link)
        val facebookLink = stringResource(string.feature_about_facebook_link)
        val githubLink = stringResource(string.feature_about_github_link)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalButton(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(helpUsLink))) }
            ) {
                Icon(TransMemoIcons.HelpUs, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(string.feature_about_help_us_title))
            }
            FilledTonalIconButton(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookLink))) }
            ) { Icon(TransMemoIcons.Facebook, contentDescription = null) }
            FilledTonalIconButton(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubLink))) }
            ) { Icon(TransMemoIcons.Github, contentDescription = null) }
        }

        Spacer(Modifier.height(16.dp))

        ChrysalideLogoFull(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(chrysalideLink))) }
        )

        Spacer(Modifier.height(16.dp))
        val infoText = stringResource(string.feature_about_menu_text)
        val linkColor = MaterialTheme.colorScheme.secondary
        val annotatedText = remember {
            buildAnnotatedString {
                append("$infoText ")
                withLink(
                    LinkAnnotation.Url(url = chrysalideLink, TextLinkStyles(SpanStyle(color = linkColor)))
                ) {
                    append(chrysalideLink)
                }
            }
        }
        Text(
            annotatedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )

        Spacer(Modifier.height(24.dp))
        var versionName = "Version ${BuildConfig.VERSION_NAME}"
        if (BuildConfig.DEBUG) {
            versionName += " - ${BuildConfig.BUILD_TYPE}"
        }

        Text(
            versionName,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )

        Spacer(Modifier.height(64.dp))
    }
}

@Composable
private fun MenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 24.dp),
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(32.dp))
        Text(text)
    }
}

@Composable
private fun MenuItem(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 24.dp),
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(32.dp))
        Text(text)
    }
}

@ThemePreviews
@Composable
private fun AboutMenuScreenPreview() {
    TransMemoTheme {
        AboutMenuScreen(
            navigateToHelp = {},
            navigateToChrysalide = {},
            navigateToContributors = {},
            navigateToLicenses = {}
        )
    }
}
