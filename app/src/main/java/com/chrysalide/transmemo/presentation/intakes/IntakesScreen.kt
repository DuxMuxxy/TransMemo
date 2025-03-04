package com.chrysalide.transmemo.presentation.intakes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.domain.model.Intake
import com.chrysalide.transmemo.domain.model.IntakeSide
import com.chrysalide.transmemo.domain.model.MeasureUnit
import com.chrysalide.transmemo.domain.model.Molecule
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.extension.doneDate
import com.chrysalide.transmemo.presentation.extension.doneDose
import com.chrysalide.transmemo.presentation.extension.doneSide
import com.chrysalide.transmemo.presentation.extension.moleculeName
import com.chrysalide.transmemo.presentation.extension.plannedDate
import com.chrysalide.transmemo.presentation.extension.plannedDose
import com.chrysalide.transmemo.presentation.extension.plannedSide
import com.chrysalide.transmemo.presentation.extension.productName
import com.chrysalide.transmemo.presentation.extension.shouldShowDoseInfo
import com.chrysalide.transmemo.presentation.extension.shouldShowPlannedDate
import com.chrysalide.transmemo.presentation.extension.shouldShowPlannedDose
import com.chrysalide.transmemo.presentation.extension.shouldShowPlannedSide
import com.chrysalide.transmemo.presentation.extension.shouldShowSideInfo
import com.chrysalide.transmemo.presentation.intakes.IntakesUiState.Empty
import com.chrysalide.transmemo.presentation.intakes.IntakesUiState.Intakes
import com.chrysalide.transmemo.presentation.intakes.IntakesUiState.Loading
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun IntakesScreen(
    viewModel: IntakesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IntakesView(uiState)
}

@Composable
private fun IntakesView(intakesUiState: IntakesUiState) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (intakesUiState) {
            Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        TransMemoIcons.IntakesUnselected,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceDim,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
            is Intakes -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            // Replace by an illustration ?
                            Icon(
                                TransMemoIcons.Intakes,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(120.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    itemsIndexed(items = intakesUiState.nextIntakes) { index, intake ->
                        NextIntakeCard(intake)
                        if (index < intakesUiState.nextIntakes.lastIndex) {
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .size(32.dp),
                            )
                        }
                    }
                    itemsIndexed(items = intakesUiState.intakes) { index, intake ->
                        IntakeCard(intake)
                        if (index < intakesUiState.intakes.lastIndex) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.MoreVert,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .size(32.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NextIntakeCard(intake: Intake) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(intake.productName(), style = MaterialTheme.typography.headlineMedium)
            Text(intake.moleculeName(), style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.feature_intakes_planned_date))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 24.dp)
                )
                Text(intake.plannedDate())
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.feature_intakes_planned_dose))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 24.dp)
                )
                Text(intake.plannedDose())
            }

            if (intake.shouldShowSideInfo()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.feature_intakes_planned_side))
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 24.dp),
                    )
                    Text(intake.plannedSide())
                }
            }
        }
    }
}

@Composable
private fun IntakeCard(intake: Intake) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(intake.productName(), style = MaterialTheme.typography.headlineMedium)
            Text(intake.moleculeName(), style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.feature_intakes_done_date))
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 24.dp)
                )
                Text(intake.doneDate())
            }
            if (intake.shouldShowPlannedDate()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.width(16.dp))
                    Text(stringResource(R.string.feature_intakes_planned_date))
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 24.dp)
                    )
                    Text(intake.plannedDate())
                }
            }

            if (intake.shouldShowDoseInfo()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.feature_intakes_done_dose))
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 24.dp)
                    )
                    Text(intake.doneDose())
                }
                if (intake.shouldShowPlannedDose()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.width(16.dp))
                        Text(stringResource(R.string.feature_intakes_planned_dose))
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 24.dp)
                        )
                        Text(intake.plannedDose())
                    }
                }
            }

            if (intake.shouldShowSideInfo()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.feature_intakes_done_side))
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 24.dp),
                    )
                    Text(intake.doneSide())
                }
                if (intake.shouldShowPlannedSide()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.width(16.dp))
                        Text(stringResource(R.string.feature_intakes_planned_side))
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 24.dp),
                        )
                        Text(intake.plannedSide())
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun IntakesScreenPreview() {
    val product = Product.default().copy(
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
    TransMemoTheme {
        IntakesView(
            intakesUiState = Intakes(
                nextIntakes = listOf(
                    Intake(
                        plannedDose = 1f,
                        realDose = 0f,
                        plannedDate = LocalDate(2020, 4, 6),
                        realDate = LocalDate(1970, 1, 1),
                        plannedSide = IntakeSide.RIGHT,
                        realSide = IntakeSide.RIGHT,
                        product = product
                    )
                ),
                intakes = listOf(
                    Intake(
                        plannedDose = 1f,
                        realDose = 1f,
                        plannedDate = LocalDate(2020, 4, 5),
                        realDate = LocalDate(2020, 4, 5),
                        plannedSide = IntakeSide.LEFT,
                        realSide = IntakeSide.LEFT,
                        product = product
                    ),
                    Intake(
                        plannedDose = 1f,
                        realDose = 1f,
                        plannedDate = LocalDate(2020, 4, 4),
                        realDate = LocalDate(2020, 4, 4),
                        plannedSide = IntakeSide.RIGHT,
                        realSide = IntakeSide.RIGHT,
                        product = product
                    )
                )
            )
        )
    }
}

@ThemePreviews
@Composable
private fun IntakesScreenEmptyPreview() {
    TransMemoTheme {
        IntakesView(intakesUiState = Empty)
    }
}
