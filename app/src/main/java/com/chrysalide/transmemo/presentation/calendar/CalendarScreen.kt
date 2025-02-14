package com.chrysalide.transmemo.presentation.calendar

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.R
import com.chrysalide.transmemo.domain.extension.formatToSystemDate
import com.chrysalide.transmemo.domain.extension.getCurrentLocalDate
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.Empty
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.IncomingEvents
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.Loading
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.design.TransMemoIcons
import com.chrysalide.transmemo.presentation.extension.daysUntilText
import com.chrysalide.transmemo.presentation.extension.typeText
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = koinViewModel(),
    navigateToIncomingEvent: (IncomingEvent) -> Unit
) {
    val calendarUiState by viewModel.calendarUiState.collectAsStateWithLifecycle()

    CalendarView(calendarUiState, onEventClick = navigateToIncomingEvent)
}

@Composable
private fun CalendarView(calendarUiState: CalendarUiState, onEventClick: (IncomingEvent) -> Unit) {
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
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    itemsIndexed(items = eventsByDate) { index, entry ->
                        DateEvents(entry.key, entry.value, onEventClick)
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
fun DateEvents(date: LocalDate, events: List<IncomingEvent>, onEventClick: (IncomingEvent.IntakeEvent) -> Unit) {
    val isToday = date == getCurrentLocalDate()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isToday) {
                Text(stringResource(R.string.global_today), style = MaterialTheme.typography.titleLarge)
            } else {
                Text("${date.formatToSystemDate()} - ${date.daysUntilText()}", style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        events.forEachIndexed { index, event ->
            when (event) {
                is IncomingEvent.EmptyContainerEvent, is IncomingEvent.ExpirationEvent ->
                    IncomingCriticalEventCard(event)
                is IncomingEvent.IntakeEvent ->
                    if (isToday) {
                        TodayIntakeEventCard(event, onClick = { onEventClick(event) })
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
private fun TodayIntakeEventCard(event: IncomingEvent.IntakeEvent, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceTint
        ),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Text(event.product.name, style = MaterialTheme.typography.titleLarge)
                Text(event.typeText())
            }
            Spacer(
                Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            OutlinedButton(
                onClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(stringResource(R.string.feature_calendar_do_intake_button))
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
            Text(event.product.name, style = MaterialTheme.typography.titleLarge)
            Text(event.typeText())
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
            Text(event.product.name, style = MaterialTheme.typography.titleLarge)
            Text(event.typeText())
        }
    }
}

@ThemePreviews
@Composable
private fun CalendarScreenLoadingPreviews() {
    TransMemoTheme {
        CalendarView(Loading, onEventClick = {})
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
                    nowDate to listOf(
                        IncomingEvent.IntakeEvent(
                            product = product,
                            date = nowDate,
                            isWarning = false,
                            isToday = true
                        )
                    ),
                    nowDate.plus(DatePeriod(days = 10)) to listOf(
                        IncomingEvent.IntakeEvent(
                            product = product,
                            date = nowDate.plus(DatePeriod(days = 10)),
                            isWarning = false
                        ),
                        IncomingEvent.IntakeEvent(
                            product = product,
                            date = nowDate.plus(DatePeriod(days = 10)),
                            isWarning = false
                        )
                    ),
                    nowDate.plus(DatePeriod(days = 15)) to listOf(
                        IncomingEvent.EmptyContainerEvent(
                            product = product,
                            date = nowDate.plus(DatePeriod(days = 15))
                        )
                    ),
                    nowDate.plus(DatePeriod(days = 17)) to listOf(
                        IncomingEvent.IntakeEvent(
                            product = product,
                            date = nowDate.plus(DatePeriod(days = 17)),
                            isWarning = true
                        )
                    ),
                    nowDate.plus(DatePeriod(days = 100)) to listOf(
                        IncomingEvent.ExpirationEvent(
                            product = product,
                            date = nowDate.plus(DatePeriod(days = 100))
                        )
                    )
                ),
            ),
            onEventClick = {}
        )
    }
}
