package com.chrysalide.transmemo.presentation.calendar

import com.chrysalide.transmemo.data.usecase.GetNextCalendarEventsUseCase
import com.chrysalide.transmemo.domain.model.IncomingEvent
import com.chrysalide.transmemo.util.FakeRepository
import com.chrysalide.transmemo.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CalendarViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeRepository<Map<LocalDate, List<IncomingEvent>>>()
    private val getNextCalendarEventsUseCase: GetNextCalendarEventsUseCase = mockk {
        coEvery { this@mockk.invoke() } returns fakeRepository.flow
    }
    private lateinit var viewModel: CalendarViewModel

    @Before
    fun setup() {
        viewModel = CalendarViewModel(getNextCalendarEventsUseCase)
    }

    @Test
    fun stateIsInitiallyLoading() =
        runTest {
            assertIs<CalendarUiState.Loading>(viewModel.uiState.value)
        }

    @Test
    fun stateIsEmptyWhenNoEventExist() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Act
            fakeRepository.emit(emptyMap())

            // Assert
            assertIs<CalendarUiState.Empty>(viewModel.uiState.value)
        }

    @Test
    fun stateIsEventsWhenEventsExist() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val map = mapOf(
                mockk<LocalDate>() to listOf<IncomingEvent>(mockk())
            )

            // Act
            fakeRepository.emit(map)

            // Assert
            assertEquals(CalendarUiState.IncomingEvents(map), viewModel.uiState.value)
        }
}
