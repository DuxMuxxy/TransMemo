package com.chrysalide.transmemo.presentation.inventory

import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.ContainerState
import com.chrysalide.transmemo.domain.model.NotificationType
import com.chrysalide.transmemo.domain.model.Product
import com.chrysalide.transmemo.presentation.notification.expiration.ExpirationAlertNotifier
import com.chrysalide.transmemo.util.FakeRepository
import com.chrysalide.transmemo.util.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InventoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeRepository<List<Container>>()
    private val databaseRepository: DatabaseRepository = mockk {
        coEvery { observeAllContainers() } returns fakeRepository.flow
        coEvery { recycleContainer(any()) } just Runs
    }
    private val expirationAlertNotifier: ExpirationAlertNotifier = mockk(relaxed = true)
    private lateinit var viewModel: InventoryViewModel

    @Before
    fun setup() {
        viewModel = InventoryViewModel(databaseRepository, expirationAlertNotifier)
    }

    @Test
    fun stateIsInitiallyLoading() =
        runTest {
            assertIs<InventoryUiState.Loading>(viewModel.uiState.value)
        }

    @Test
    fun stateIsEmptyWhenNoContainerExist() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Act
            fakeRepository.emit(emptyList())

            // Assert
            assertIs<InventoryUiState.Empty>(viewModel.uiState.value)
        }

    @Test
    fun stateIsEmptyWhenNoOpenContainerExist() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val product = Product.default().copy(inUse = true)
            val containers = listOf(
                Container.new(product).copy(state = ContainerState.EMPTY)
            )

            // Act
            fakeRepository.emit(containers)

            // Assert
            assertIs<InventoryUiState.Empty>(viewModel.uiState.value)
        }

    @Test
    fun stateIsEmptyWhenNoProductIsInUse() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val product = Product.default().copy(inUse = false)
            val containers = listOf(Container.new(product))

            // Act
            fakeRepository.emit(containers)

            // Assert
            assertIs<InventoryUiState.Empty>(viewModel.uiState.value)
        }

    @Test
    fun stateIsContainersWhenOpenContainersExist() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }

            // Arrange
            val product = Product.default().copy(inUse = true)
            val containers = listOf(Container.new(product))

            // Act
            fakeRepository.emit(containers)

            // Assert
            assertEquals(InventoryUiState.Containers(containers), viewModel.uiState.value)
        }

    @Test
    fun callRecycleContainerWhenInvoke() {
        // Arrange
        val container = Container.new(Product.default())

        // Act
        viewModel.recycleContainer(container)

        // Assert
        coVerify { databaseRepository.recycleContainer(container) }
        verify { expirationAlertNotifier.cancelNotification(NotificationType.EXPIRATION.notificationId(container.product.id)) }
    }
}
