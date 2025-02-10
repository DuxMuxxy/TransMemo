package com.chrysalide.transmemo.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.data.usecase.GetNextCalendarEventsUseCase
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.IncomingEvents
import com.chrysalide.transmemo.presentation.calendar.CalendarUiState.Loading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarViewModel(
    getNextCalendarEventsUseCase: GetNextCalendarEventsUseCase
) : ViewModel() {
    private val _calendarUiState = MutableStateFlow<CalendarUiState>(Loading)
    val calendarUiState: StateFlow<CalendarUiState> = _calendarUiState

    init {
        viewModelScope.launch {
            val events = getNextCalendarEventsUseCase()
            _calendarUiState.update { IncomingEvents(events) }
        }
    }
}

sealed interface CalendarUiState {
    data object Loading : CalendarUiState

    data class IncomingEvents(
        val incomingEvents: List<IncomingEvent>
    ) : CalendarUiState
}
