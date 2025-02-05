package com.chrysalide.transmemo.presentation.containers

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.MeasureUnit
import com.chrysalide.transmemo.domain.model.Molecule
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.containers.ContainersUiState.Containers
import com.chrysalide.transmemo.presentation.containers.ContainersUiState.Loading
import com.chrysalide.transmemo.presentation.design.LinearProgressBar
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.extension.capacity
import com.chrysalide.transmemo.presentation.extension.expirationDate
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.extension.openDate
import com.chrysalide.transmemo.presentation.extension.productName
import com.chrysalide.transmemo.presentation.extension.remainingCapacity
import com.chrysalide.transmemo.presentation.extension.usedCapacity
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import dev.sergiobelda.compose.vectorize.images.Images
import dev.sergiobelda.compose.vectorize.images.icons.rounded.Medication
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun ContainersScreen(
    viewModel: ContainersViewModel = koinViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val containersUiState by viewModel.containersUiState.collectAsStateWithLifecycle()
    var showRecycleContainerDialog by remember { mutableStateOf(false) }
    var showRecycleSuccessSnackbar by remember { mutableStateOf(false) }
    var containerToRecycle by remember { mutableStateOf<Container?>(null) }

    /*if (showRecycleContainerDialog) {
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

    val recycleContainerSuccessText = stringResource(string.feature_containers_recycle_success)
    LaunchedEffect(showRecycleSuccessSnackbar) {
        if (showRecycleSuccessSnackbar) {
            onShowSnackbar(recycleContainerSuccessText, null)
            showRecycleSuccessSnackbar = false
        }
    }*/

    ContainersView(
        containersUiState,
        recycleContainer = {
            containerToRecycle = it
            showRecycleContainerDialog = true
        }
    )
}

@Composable
private fun ContainersView(
    containersUiState: ContainersUiState,
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

            is Containers -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 88.dp)
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            // Replace by an illustration ?
                            Icon(
                                Images.Icons.Rounded.Medication,
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(container.productName(), style = MaterialTheme.typography.headlineMedium)
                Text(container.moleculeName(), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(string.feature_containers_open_date))
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 24.dp)
                    )
                    Text(container.openDate())
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(string.feature_containers_expiration_date))
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 24.dp)
                    )
                    Text(container.expirationDate())
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(string.feature_containers_total_capacity))
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 24.dp)
                    )
                    Text(container.capacity())
                }
            }
            HorizontalDivider()
            Box {
                var progress by remember { mutableFloatStateOf(1F) }
                val progressAnimation by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(durationMillis = 1_500, easing = FastOutSlowInEasing)
                )
                LaunchedEffect(Unit) {
                    progress = (container.product.capacity - container.usedCapacity) / container.product.capacity
                }
                LinearProgressBar(
                    progress = { progressAnimation },
                    height = 120.dp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(string.feature_containers_remaining_capacity),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            container.remainingCapacity(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            stringResource(string.feature_containers_used_capacity),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            container.usedCapacity(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ContainersScreenLoadingPreviews() {
    TransMemoTheme {
        ContainersView(Loading, {})
    }
}

@ThemePreviews
@Composable
private fun ContainersScreenListPreviews() {
    TransMemoTheme {
        ContainersView(
            Containers(
                containers = listOf(
                    Container(
                        usedCapacity = 3f,
                        openDate = LocalDate(2020, 4, 5),
                        state = 2,
                        product = Product(
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
