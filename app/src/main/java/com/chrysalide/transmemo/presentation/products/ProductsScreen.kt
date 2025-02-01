package com.chrysalide.transmemo.presentation.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R.plurals
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.core.model.entities.ProductEntity
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.extension.containerCapacity
import com.chrysalide.transmemo.presentation.extension.dosePerIntake
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.products.ProductsUiState.Loading
import com.chrysalide.transmemo.presentation.products.ProductsUiState.Products
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = koinViewModel()
) {
    val productsUiState by viewModel.productsUiState.collectAsStateWithLifecycle()

    ProductsView(productsUiState)
}

@Composable
private fun ProductsView(productsUiState: ProductsUiState) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (productsUiState) {
            is Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator()
                }
            }

            is Products -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                ) {
                    itemsIndexed(items = productsUiState.products) { index, product ->
                        ProductCard(product)
                        if (index < productsUiState.products.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: ProductEntity) {
    var isExtented by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { isExtented = !isExtented },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleLarge)
            Text(product.moleculeName())

            AnimatedVisibility(isExtented) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(string.feature_products_beeing_used))
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(checked = product.inUse, enabled = false, onCheckedChange = {})
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(string.feature_products_intake_periodicity))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(pluralStringResource(plurals.global_days_text_template, product.intakeInterval, product.intakeInterval))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(string.feature_products_handle_side))
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(checked = product.handleSide, enabled = false, onCheckedChange = {})
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(string.feature_products_dosing_per_intake))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(product.dosePerIntake())
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(string.feature_products_capacity))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(product.containerCapacity())
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(string.feature_products_expiration_after_opening))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(pluralStringResource(plurals.global_days_text_template, product.expirationDays, product.expirationDays))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(string.feature_products_alert_delay))
                        Spacer(modifier = Modifier.weight(1f))
                        Text(pluralStringResource(plurals.global_days_text_template, product.alertDelay, product.alertDelay))
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ProductsScreenLoadingPreviews() {
    TransMemoTheme {
        ProductsView(Loading)
    }
}

@ThemePreviews
@Composable
private fun ProductsScreenListPreviews() {
    TransMemoTheme {
        ProductsView(
            Products(
                products = listOf(
                    ProductEntity(
                        name = "Testosterone",
                        molecule = 9,
                        unit = 4,
                        dosePerIntake = 1f,
                        capacity = 2f,
                        expirationDays = 365,
                        intakeInterval = 21,
                        alertDelay = 1,
                        handleSide = true,
                        inUse = true,
                        notifications = 15
                    )
                )
            )
        )
    }
}
