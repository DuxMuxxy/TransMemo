package com.chrysalide.transmemo.presentation.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.presentation.design.TMSubScreen
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme

@Composable
fun AboutLicensesScreen(navigateUp: () -> Unit) {
    TMSubScreen(
        titleRes = string.feature_about_licenses_title,
        iconEither = TransMemoIcons.Licenses to null,
        navigateUp = navigateUp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text("Libraries:", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp)
            ) {
                Text("- Koin, Copyright 2025 Koin & Kotzilla")
                Text("- Compose Vectorize, Copyright 2024 Sergio Belda")
                Text("- Kotlinter Gradle, Copyright Jeremy Mailen")
                Text("- MockK, Copyright MockK")
            }

            Spacer(Modifier.height(32.dp))
            Text("License:", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GNU General Public License version 3 or later")
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "This program is Free Software: You can use, study share and improve it at your will. Specifically you can redistribute and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AboutLicensesScreenPreview() {
    TransMemoTheme {
        AboutLicensesScreen({})
    }
}
