package com.chrysalide.transmemo.presentation.about

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
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
            val context = LocalContext.current
            val chrysalideLink = stringResource(string.global_chrysalide_asso_url)
            val chrysalideMail = stringResource(string.global_chrysalide_asso_mail)
            val linkColor = MaterialTheme.colorScheme.secondary
            val annotatedText = remember {
                buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(
                            chrysalideLink,
                            TextLinkStyles(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline))
                        )
                    ) { append(chrysalideLink) }

                    withLink(
                        LinkAnnotation.Clickable(
                            tag = chrysalideMail,
                            TextLinkStyles(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline))
                        ) { openMailIntent(context, chrysalideMail) }
                    ) { append(chrysalideMail) }
                }
            }
            Text(annotatedText)
        }
    }
}

private fun openMailIntent(
    context: Context,
    email: String
) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
    }
    context.startActivity(intent)
}

@ThemePreviews
@Composable
private fun AboutChrysalideScreenPreview() {
    TransMemoTheme {
        AboutChrysalideScreen({})
    }
}
