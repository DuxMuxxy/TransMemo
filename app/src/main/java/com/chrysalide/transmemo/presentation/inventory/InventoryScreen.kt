package com.chrysalide.transmemo.presentation.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.extension.formatToSystemDate
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.ContainerState
import com.chrysalide.transmemo.domain.model.MeasureUnit
import com.chrysalide.transmemo.domain.model.Molecule
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.design.LinearProgressBar
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.extension.capacity
import com.chrysalide.transmemo.presentation.extension.expirationDateString
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.extension.openDate
import com.chrysalide.transmemo.presentation.extension.productName
import com.chrysalide.transmemo.presentation.extension.remainingCapacity
import com.chrysalide.transmemo.presentation.extension.usedCapacity
import com.chrysalide.transmemo.presentation.inventory.InventoryUiState.Containers
import com.chrysalide.transmemo.presentation.inventory.InventoryUiState.Empty
import com.chrysalide.transmemo.presentation.inventory.InventoryUiState.Loading
import com.chrysalide.transmemo.presentation.inventory.recycle.AskRecycleContainerDialog
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import dev.sergiobelda.compose.vectorize.images.Images
import dev.sergiobelda.compose.vectorize.images.icons.outlined.Recycling
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = koinViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showRecycleContainerDialog by remember { mutableStateOf(false) }
    var showRecycleSuccessSnackbar by remember { mutableStateOf(false) }
    var containerToRecycle by remember { mutableStateOf<Container?>(null) }

    if (showRecycleContainerDialog) {
        AskRecycleContainerDialog(
            onDismiss = {
                containerToRecycle = null
                showRecycleContainerDialog = false
            },
            onConfirm = {
                containerToRecycle?.let { viewModel.recycleContainer(it) }
                showRecycleContainerDialog = false
                showRecycleSuccessSnackbar = true
            }
        )
    }

    val recycleContainerSuccessText = stringResource(string.feature_inventory_recycle_success)
    LaunchedEffect(showRecycleSuccessSnackbar) {
        if (showRecycleSuccessSnackbar) {
            onShowSnackbar(recycleContainerSuccessText, null)
            showRecycleSuccessSnackbar = false
        }
    }

    InventoryView(
        uiState,
        recycleContainer = {
            containerToRecycle = it
            showRecycleContainerDialog = true
        }
    )
}

@Composable
private fun InventoryView(
    containersUiState: InventoryUiState,
    recycleContainer: (Container) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (containersUiState) {
            is Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            is Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        TransMemoIcons.InventoryUnselected,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceDim,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            is Containers -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 88.dp)
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            // Replace by an illustration ?
                            Icon(
                                TransMemoIcons.Inventory,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    itemsIndexed(items = containersUiState.containers) { index, container ->
                        ContainerCard(container, recycleContainer)
                        if (index < containersUiState.containers.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContainerCard(
    container: Container,
    recycleContainer: (Container) -> Unit
) {
    var isExtented by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { isExtented = !isExtented }
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(container.productName(), style = MaterialTheme.typography.headlineMedium)
                        Text(container.moleculeName(), style = MaterialTheme.typography.titleMedium)
                    }
                    val recycleButtonContentDescription = stringResource(string.feature_inventory_recycle_button_content_description)
                    IconButton(onClick = { recycleContainer(container) }, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(Images.Icons.Outlined.Recycling, contentDescription = recycleButtonContentDescription)
                    }
                }

                AnimatedVisibility(isExtented) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(string.feature_inventory_open_date))
                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically)
                                    .padding(horizontal = 24.dp)
                            )
                            Text(container.openDate())
                        }

                        if (container.product.expirationDays > 0) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(string.feature_inventory_expiration_date))
                                HorizontalDivider(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically)
                                        .padding(horizontal = 24.dp),
                                )
                                Text(container.expirationDateString())
                            }
                        }

                        if (container.product.capacity > container.product.dosePerIntake) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(string.feature_inventory_empty_date))
                                HorizontalDivider(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically)
                                        .padding(horizontal = 24.dp),
                                )
                                Text(container.emptyDate().formatToSystemDate())
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(string.feature_inventory_total_capacity))
                                HorizontalDivider(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically)
                                        .padding(horizontal = 24.dp),
                                )
                                Text(container.capacity())
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val icon = if (isExtented) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown
                Icon(icon, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (container.product.capacity > container.product.dosePerIntake) {
                HorizontalDivider()
                Box {
                    var progress by remember { mutableFloatStateOf(1F) }
                    val progressAnimation by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(durationMillis = 1_500, easing = FastOutSlowInEasing),
                    )
                    LaunchedEffect(Unit) {
                        progress = (container.product.capacity - container.usedCapacity) / container.product.capacity
                    }
                    LinearProgressBar(
                        progress = { progressAnimation },
                        height = 120.dp,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                stringResource(string.feature_inventory_remaining_capacity),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                container.remainingCapacity(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End,
                        ) {
                            Text(
                                stringResource(string.feature_inventory_used_capacity),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Text(
                                container.usedCapacity(),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.End,
                            )
                        }
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun InventoryScreenListPreviews() {
    TransMemoTheme {
        InventoryView(
            Containers(
                containers = listOf(
                    Container(
                        usedCapacity = 3f,
                        openDate = LocalDate(2020, 4, 5),
                        state = ContainerState.OPEN,
                        product = Product.default().copy(
                            name = "Testosterone",
                            molecule = Molecule.TESTOSTERONE,
                            unit = MeasureUnit.MILLILITER,
                            dosePerIntake = 1f,
                            capacity = 10f,
                            expirationDays = 365,
                            intakeInterval = 21,
                            alertDelay = 1,
                            handleSide = true,
                            inUse = true,
                            notifications = 15
                        )
                    )
                )
            ),
            recycleContainer = {}
        )
    }
}

@ThemePreviews
@Composable
private fun InventoryScreenNoCapacityPreviews() {
    TransMemoTheme {
        InventoryView(
            Containers(
                containers = listOf(
                    Container(
                        usedCapacity = 3f,
                        openDate = LocalDate(2020, 4, 5),
                        state = ContainerState.OPEN,
                        product = Product.default().copy(
                            name = "Testosterone",
                            molecule = Molecule.TESTOSTERONE,
                            unit = MeasureUnit.MILLILITER,
                            dosePerIntake = 0f,
                            capacity = 0f,
                            expirationDays = 365,
                            intakeInterval = 21,
                            alertDelay = 1,
                            handleSide = true,
                            inUse = true,
                            notifications = 15
                        )
                    )
                )
            ),
            recycleContainer = {}
        )
    }
}

@ThemePreviews
@Composable
private fun InventoryScreenOneShotPreviews() {
    TransMemoTheme {
        InventoryView(
            Containers(
                containers = listOf(
                    Container(
                        usedCapacity = 3f,
                        openDate = LocalDate(2020, 4, 5),
                        state = ContainerState.OPEN,
                        product = Product.default().copy(
                            name = "Testosterone",
                            molecule = Molecule.TESTOSTERONE,
                            unit = MeasureUnit.MILLILITER,
                            dosePerIntake = 1f,
                            capacity = 1f,
                            expirationDays = 365,
                            intakeInterval = 21,
                            alertDelay = 1,
                            handleSide = true,
                            inUse = true,
                            notifications = 15
                        )
                    )
                )
            ),
            recycleContainer = {}
        )
    }
}

@ThemePreviews
@Composable
private fun InventoryScreenLoadingPreviews() {
    TransMemoTheme {
        InventoryView(Loading, {})
    }
}

@ThemePreviews
@Composable
private fun InventoryScreenEmptyPreviews() {
    TransMemoTheme {
        InventoryView(Empty, {})
    }
}
