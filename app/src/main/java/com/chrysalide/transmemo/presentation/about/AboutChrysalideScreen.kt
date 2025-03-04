package com.chrysalide.transmemo.presentation.about

import android.text.Html
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.util.Either
import com.chrysalide.transmemo.presentation.design.TMSubScreen
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.extension.convertHtmlSpannedToAnnotatedString
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AboutChrysalideScreen(navigateUp: () -> Unit) {
    TMSubScreen(
        titleRes = string.feature_about_chrysalide_asso_title,
        iconEither = Either.Right(painterResource(R.drawable.logo_chrysalide)),
        navigateUp = navigateUp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                Html
                    .fromHtml(
                        stringResource(string.feature_about_chrysalide_asso_content),
                        Html.FROM_HTML_MODE_COMPACT
                    ).convertHtmlSpannedToAnnotatedString()
            )
            val chrysalideLink = stringResource(string.global_chrysalide_asso_url)
            val chrysalideMail = stringResource(string.global_chrysalide_asso_mail)
            val linkColor = MaterialTheme.colorScheme.secondary
            val annotatedText = remember {
                buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(url = chrysalideLink, TextLinkStyles(SpanStyle(color = linkColor)))
                    ) {
                        append(chrysalideLink)
                    }
                    withLink(
                        LinkAnnotation.Url(url = chrysalideMail, TextLinkStyles(SpanStyle(color = linkColor)))
                    ) {
                        append(chrysalideMail)
                    }
                }
            }
            Text(annotatedText)
        }
    }
}

@ThemePreviews
@Composable
private fun AboutChrysalideScreenPreview() {
    TransMemoTheme {
        AboutChrysalideScreen({})
    }
}
