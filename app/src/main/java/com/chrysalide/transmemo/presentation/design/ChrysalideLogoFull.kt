package com.chrysalide.transmemo.presentation.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun ChrysalideLogoFull(modifier: Modifier = Modifier) {
    val resource = if (isSystemInDarkTheme()) R.drawable.logo_chrysalide_full_night else R.drawable.logo_chrysalide_full
    Image(
        painterResource(resource),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
    )
}

@ThemePreviews
@Composable
private fun ChrysalideLogoFullPreview() {
    TransMemoTheme {
        ChrysalideLogoFull()
    }
}
