package com.chrysalide.transmemo.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.IncomingEvents
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.Loading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import kotlin.time.Duration.Companion.seconds

class CalendarViewModel : ViewModel() {
    val calendarUiState: StateFlow<CalendarUiState> = MutableStateFlow(Loading)
        .map {
            // MOCK
            delay(2.seconds.inWholeMilliseconds)
            IncomingEvents(
                incomingEvents = listOf(
                    IncomingEvent("Testostérone", "Prise à venir", LocalDate(2025, 4, 1)),
                    IncomingEvent("Testostérone", "Prise à venir", LocalDate(2025, 5, 1)),
                    IncomingEvent("Testostérone", "Prise à venir", LocalDate(2025, 6, 1)),
                    IncomingEvent("Testostérone", "Prise à venir", LocalDate(2025, 7, 1)),
                    IncomingEvent("Testostérone", "Péremption de la boîte", LocalDate(2025, 8, 1)),
                    IncomingEvent("Testostérone", "Boîte vide", LocalDate(2025, 8, 15)),
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = Loading,
        )
}

sealed interface CalendarUiState {
    data object Loading : CalendarUiState

    data class IncomingEvents(
        val incomingEvents: List<IncomingEvent>
    ) : CalendarUiState
}
