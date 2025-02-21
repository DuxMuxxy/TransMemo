package com.chrysalide.transmemo.presentation.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.util.Either
import com.chrysalide.transmemo.presentation.design.TMSubScreen
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AboutChrysalideScreen(navigateUp: () -> Unit) {
    TMSubScreen(
        titleRes = string.feature_about_chrysalide_asso_title,
        iconEither = Either.Right(painterResource(R.drawable.logo_chrysalide)),
        navigateUp = navigateUp
    ) {
    }
}

@ThemePreviews
@Composable
private fun AboutChrysalideScreenPreview() {
    TransMemoTheme {
        AboutChrysalideScreen({})
    }
}
