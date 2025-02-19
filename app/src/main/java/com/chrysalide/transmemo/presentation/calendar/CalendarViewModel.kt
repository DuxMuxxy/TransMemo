package com.chrysalide.transmemo.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.GetNextCalendarEventsUseCase
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.IncomingEvents
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.Loading
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import kotlin.time.Duration.Companion.seconds

class CalendarViewModel(
    getNextCalendarEventsUseCase: GetNextCalendarEventsUseCase
) : ViewModel() {
    val uiState: StateFlow<CalendarUiState> = getNextCalendarEventsUseCase()
        .map { events ->
            if (events.isNotEmpty()) {
                IncomingEvents(events)
            } else {
                CalendarUiState.Empty
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = Loading
        )
}

sealed interface CalendarUiState {
    data object Loading : CalendarUiState

    data object Empty : CalendarUiState

    data class IncomingEvents(
        val incomingEvents: Map<LocalDate, List<IncomingEvent>>
    ) : CalendarUiState
}
