package com.chrysalide.transmemo.presentation.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                ) {
                    itemsIndexed(items = calendarUiState.incomingEvents) { index, event ->
                        when (event) {
                            is IncomingEvent.EmptyContainerEvent, is IncomingEvent.ExpirationEvent ->
                                IncomingCriticalEventCard(event)
                            is IncomingEvent.IntakeEvent ->
                                if (event.isToday) {
                                    TodayIntakeEventCard(event, onClick = { onEventClick(event) })
                                } else {
                                    IncomingIntakeEventCard(event)
                                }
                        }
                        if (index < calendarUiState.incomingEvents.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
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
        onClick = { if (event.isToday) onClick() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.product.name, style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.global_today))
            Text(event.typeText())
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
            Text(event.date.formatToSystemDate())
            Text(event.daysUntilText())
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
            Text(event.date.formatToSystemDate())
            Text(event.daysUntilText())
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
                incomingEvents = listOf(
                    IncomingEvent.IntakeEvent(
                        product = product,
                        date = nowDate,
                        isWarning = false,
                        isToday = true
                    ),
                    IncomingEvent.IntakeEvent(
                        product = product,
                        date = nowDate.plus(DatePeriod(days = 10)),
                        isWarning = false
                    ),
                    IncomingEvent.EmptyContainerEvent(
                        product = product,
                        date = nowDate.plus(DatePeriod(days = 15))
                    ),
                    IncomingEvent.IntakeEvent(
                        product = product,
                        date = nowDate.plus(DatePeriod(days = 17)),
                        isWarning = true
                    ),
                    IncomingEvent.ExpirationEvent(
                        product = product,
                        date = nowDate.plus(DatePeriod(days = 100))
                    )
                ),
            ),
            onEventClick = {}
        )
    }
}
