package com.chrysalide.transmemo.presentation.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.presentation.design.ChrysalideLogoFull
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.extension.getAppVersionName
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AboutMenuScreen(
    navigateToHelp: () -> Unit,
    navigateToChrysalide: () -> Unit,
    navigateToContributors: () -> Unit,
    navigateToLicenses: () -> Unit
) {
    val context = LocalContext.current
    val appVersion by remember { mutableStateOf(context.getAppVersionName()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MenuItem(
            stringResource(R.string.feature_about_help_title),
            icon = TransMemoIcons.Help,
            onClick = navigateToHelp,
        )
        HorizontalDivider()
        MenuItem(
            stringResource(R.string.feature_about_chrysalide_asso_title),
            icon = painterResource(R.drawable.logo_chrysalide), // TODO chrysalide icon svg ?
            onClick = navigateToChrysalide,
        )
        HorizontalDivider()
        val helpUsLink = stringResource(R.string.feature_about_help_us_link)
        MenuItem(
            stringResource(R.string.feature_about_help_us_title),
            icon = TransMemoIcons.HelpUs,
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(helpUsLink)))
            },
        )
        HorizontalDivider()
        val facebookLink = stringResource(R.string.feature_about_facebook_link)
        MenuItem(
            stringResource(R.string.feature_about_facebook_title),
            icon = TransMemoIcons.Facebook,
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookLink)))
            },
        )
        HorizontalDivider()
        MenuItem(
            stringResource(R.string.feature_about_contributors_title),
            icon = TransMemoIcons.Contributors,
            onClick = navigateToContributors,
        )
        HorizontalDivider()
        MenuItem(
            stringResource(R.string.feature_about_licenses_title),
            icon = TransMemoIcons.Licenses,
            onClick = navigateToLicenses,
        )
        HorizontalDivider()

        Spacer(Modifier.height(42.dp))

        ChrysalideLogoFull(modifier = Modifier.fillMaxWidth(0.7f))

        Spacer(Modifier.height(16.dp))
        val infoText = stringResource(R.string.feature_about_menu_text)
        val link = stringResource(R.string.feature_about_menu_link)
        val linkColor = MaterialTheme.colorScheme.secondary
        val annotatedText = remember {
            buildAnnotatedString {
                append("$infoText ")
                withLink(
                    LinkAnnotation.Url(url = link, TextLinkStyles(SpanStyle(color = linkColor)))
                ) {
                    append(link)
                }
            }
        }
        Text(
            annotatedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )

        appVersion?.let {
            Spacer(Modifier.height(24.dp))
            Text(
                "Version $appVersion",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }

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
        Icon(icon, contentDescription = null)
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
