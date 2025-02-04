package com.chrysalide.transmemo.presentation.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.IncomingEvents
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.Loading
import com.chrysalide.transmemo.presentation.design.ThemePreviews
import com.chrysalide.transmemo.presentation.theme.TransMemoTheme
import kotlinx.datetime.LocalDate
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

            is IncomingEvents -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                ) {
                    itemsIndexed(items = calendarUiState.incomingEvents) { index, event ->
                        IncomingEventCard(event, onClick = { onEventClick(event) })
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
private fun IncomingEventCard(event: IncomingEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleLarge)
            Text(event.description)
            Text(event.date.toString())
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
    TransMemoTheme {
        CalendarView(
            IncomingEvents(
                incomingEvents = listOf(
                    IncomingEvent("Testostérone", "Prise à venir", LocalDate(2025, 4, 1)),
                    IncomingEvent("Testostérone", "Prise à venir", LocalDate(2025, 5, 1)),
                    IncomingEvent("Testostérone", "Prise à venir", LocalDate(2025, 6, 1)),
                    IncomingEvent("Testostérone", "Prise à venir", LocalDate(2025, 7, 1)),
                    IncomingEvent("Testostérone", "Péremption de la boîte", LocalDate(2025, 8, 1)),
                    IncomingEvent("Testostérone", "Boîte vide", LocalDate(2025, 8, 15)),
                ),
            ),
            onEventClick = {}
        )
    }
}
