package com.chrysalide.transmemo.presentation.calendar

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R.string
import com.chrysalide.transmemo.domain.extension.formatToSystemDate
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.extension.toEpochMillis
import com.chrysalide.transmemo.domain.model.DateIntakeEvent
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.Empty
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.IncomingEvents
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.Loading
import com.chrysalide.transmemo.presentation.calendar.dointake.DoIntakeModal
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.extension.dayTimeOfIntake
import com.chrysalide.transmemo.presentation.extension.daysLateText
import com.chrysalide.transmemo.presentation.extension.daysUntilText
import com.chrysalide.transmemo.presentation.extension.typeText
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var doIntakeModalProduct by remember { mutableStateOf<DateIntakeEvent?>(null) }

    CalendarView(
        uiState,
        doIntake = { doIntakeModalProduct = it }
    )

    if (doIntakeModalProduct != null) {
        DoIntakeModal(
            intakeEvent = doIntakeModalProduct!!,
            onDismiss = {
                doIntakeModalProduct = null
            }
        )
    }
}

@Composable
private fun CalendarView(calendarUiState: CalendarUiState, doIntake: (DateIntakeEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (calendarUiState) {
            is Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
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
                        TransMemoIcons.Calendar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceDim,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            is IncomingEvents -> {
                val eventsByDate = calendarUiState.incomingEvents.entries.toList()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                ) {
                    itemsIndexed(
                        items = eventsByDate,
                        key = { _, entry -> entry.key.toEpochMillis() }
                    ) { index, entry ->
                        DateEvents(entry.key, entry.value, doIntake)
                        if (index < eventsByDate.lastIndex) {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LazyItemScope.DateEvents(
    date: LocalDate,
    events: List<IncomingEvent>,
    doIntake: (DateIntakeEvent) -> Unit
) {
    val isLate = date < getCurrentLocalDate()
    val isYesterday = date == getCurrentLocalDate().minus(DatePeriod(days = 1))
    val isToday = date == getCurrentLocalDate()
    val isTomorrow = date == getCurrentLocalDate().plus(DatePeriod(days = 1))
    val isDoable = date <= getCurrentLocalDate()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateItem()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            when {
                isYesterday -> {
                    Text(
                        stringResource(string.global_yesterday),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                isLate -> {
                    Text(
                        stringResource(string.feature_calendar_date_template, date.formatToSystemDate(), date.daysLateText()),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                isTomorrow -> Text(stringResource(string.global_tomorrow), style = MaterialTheme.typography.titleMedium)
                isToday -> Text(stringResource(string.global_today), style = MaterialTheme.typography.titleLarge)
                else -> {
                    Text(
                        stringResource(string.feature_calendar_date_template, date.formatToSystemDate(), date.daysUntilText()),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        events.forEachIndexed { index, event ->
            when (event) {
                is IncomingEvent.EmptyContainerEvent, is IncomingEvent.ExpirationEvent ->
                    IncomingCriticalEventCard(event)

                is IncomingEvent.IntakeEvent ->
                    if (isDoable) {
                        DoableIntakeEventCard(
                            event,
                            doIntake = {
                                doIntake(DateIntakeEvent(date, event))
                            }
                        )
                    } else {
                        IncomingIntakeEventCard(event)
                    }
            }
            if (index < events.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DoableIntakeEventCard(event: IncomingEvent.IntakeEvent, doIntake: () -> Unit) {
    val containerColor = when {
        event.isLate -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceTint
    }
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        onClick = doIntake
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Text(event.typeText(), style = MaterialTheme.typography.titleLarge)
                Text(event.product.name)
                Text(event.product.dayTimeOfIntake())
            }
            Spacer(
                Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            val contentColor = when {
                event.isLate -> MaterialTheme.colorScheme.onTertiaryContainer
                else -> MaterialTheme.colorScheme.onPrimary
            }
            OutlinedButton(
                doIntake,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
            ) {
                Text(stringResource(string.feature_calendar_do_intake_button))
            }
        }
    }
}

@Composable
private fun IncomingIntakeEventCard(event: IncomingEvent.IntakeEvent) {
    val containerColor = when {
        event.isWarning -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = containerColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.typeText(), style = MaterialTheme.typography.titleLarge)
            Text(event.product.name)
            Text(event.product.dayTimeOfIntake())
        }
    }
}

@Composable
private fun IncomingCriticalEventCard(event: IncomingEvent) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.typeText(), style = MaterialTheme.typography.titleLarge)
            Text(event.product.name)
        }
    }
}

@ThemePreviews
@Composable
private fun CalendarScreenLoadingPreviews() {
    TransMemoTheme {
        CalendarView(Loading, doIntake = {})
    }
}

@ThemePreviews
@Composable
private fun CalendarScreenListPreviews() {
    val product = Product.default().copy(
        name = "Testosterone"
    )
    val nowDate = getCurrentLocalDate()
    TransMemoTheme {
        CalendarView(
            IncomingEvents(
                incomingEvents = mapOf(
                    nowDate.minus(DatePeriod(days = 4)) to listOf(
                        IncomingEvent.IntakeEvent(
                            product = product,
                            isLate = true
                        )
                    ),
                    nowDate.minus(DatePeriod(days = 1)) to listOf(
                        IncomingEvent.IntakeEvent(
                            product = product,
                            isLate = true
                        )
                    ),
                    nowDate to listOf(
                        IncomingEvent.IntakeEvent(
                            product = product,
                            isToday = true
                        )
                    ),
                    nowDate.plus(DatePeriod(days = 1)) to listOf(
                        IncomingEvent.IntakeEvent(product)
                    ),
                    nowDate.plus(DatePeriod(days = 10)) to listOf(
                        IncomingEvent.IntakeEvent(product),
                        IncomingEvent.IntakeEvent(product)
                    ),
                    nowDate.plus(DatePeriod(days = 15)) to listOf(
                        IncomingEvent.EmptyContainerEvent(product)
                    ),
                    nowDate.plus(DatePeriod(days = 17)) to listOf(
                        IncomingEvent.IntakeEvent(
                            product = product,
                            isWarning = true
                        )
                    ),
                    nowDate.plus(DatePeriod(days = 100)) to listOf(
                        IncomingEvent.ExpirationEvent(product)
                    )
                ),
            ),
            doIntake = {}
        )
    }
}
