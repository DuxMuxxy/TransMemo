package com.chrysalide.transmemo.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrysalide.transmemo.domain.boundary.DatabaseRepository
import com.chrysalide.transmemo.domain.model.Container
import com.chrysalide.transmemo.domain.model.ContainerState
import com.chrysalide.transmemo.domain.model.NotificationType
import com.chrysalide.transmemo.presentation.inventory.InventoryUiState.Containers
import com.chrysalide.transmemo.presentation.notification.expiration.ExpirationAlertNotifier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class InventoryViewModel(
    private val databaseRepository: DatabaseRepository,
    private val expirationAlertNotifier: ExpirationAlertNotifier
) : ViewModel() {
    val uiState: StateFlow<InventoryUiState> = databaseRepository
        .observeAllContainers()
        .map {
            it.filter { container -> container.product.inUse && container.state == ContainerState.OPEN }.let { filtered ->
                if (filtered.isNotEmpty()) {
                    Containers(filtered)
                } else {
                    InventoryUiState.Empty
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            initialValue = InventoryUiState.Loading
        )

    fun recycleContainer(container: Container) {
        viewModelScope.launch {
            databaseRepository.recycleContainer(container)
            expirationAlertNotifier.cancelNotification(NotificationType.EXPIRATION.notificationId(container.product.id))
        }
    }

    fun editRemainingCapacity(container: Container) {
        viewModelScope.launch {
            databaseRepository.updateContainer(container)
        }
    }
}

sealed interface InventoryUiState {
    data object Loading : InventoryUiState

    data object Empty : InventoryUiState

    data class Containers(
        val containers: List<Container>
    ) : InventoryUiState
}
